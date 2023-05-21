#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/wait.h>
#include <string.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <sys/mman.h>
#include <signal.h>
#include <pthread.h>
#include <semaphore.h>
#include <fcntl.h>
#include <execinfo.h>
#include <sys/types.h>
#include <time.h>
#include <stdatomic.h>
#include "utils.h"
#include "err.h"
#include "queue.h"

#define BUFFER_SIZE 4096
#define MAX_LINE 1024

typedef struct {
  pid_t *pid;
  int *task_id;
  int *stdout_fd;
  int *stderr_fd;
  int *ended;
  int *howMany;
  pthread_mutex_t *outMutex;
  pthread_mutex_t *errMutex;
  pthread_t *outThread;
  pthread_t *errThread;
  char *outBuffer;
  char *errBuffer;
} task_t;

atomic_int *task_count;
atomic_int *working;

task_t tasks[BUFFER_SIZE];

struct queue* q;
pthread_mutex_t *mutex;
pthread_t *signal_manager;
pthread_cond_t *cond;
pthread_cond_t *signalsDone;

/* Main function of thread managing a task's output */
void* output_writer_main(void* data) {
  ASSERT_ZERO(pthread_mutex_lock(mutex));
  int task_id = *((int*) data);
  int fd = *(tasks[task_id].stdout_fd);
  FILE *fp;
  fp = fdopen(fd, "r");
  char line[MAX_LINE];
  char* dest = tasks[task_id].outBuffer;
  pthread_mutex_t *outMutex = tasks[task_id].outMutex;
  *(tasks[task_id].howMany) = *(tasks[task_id].howMany) + 1;
  if (*(tasks[task_id].howMany) == 2) {
    kill(*(tasks[task_id].pid), SIGUSR2);
    ASSERT_ZERO(pthread_cond_signal(cond));
  }
  ASSERT_ZERO(pthread_mutex_unlock(mutex));
  if (fp == NULL) {
    syserr("fdopen");
    exit(1);
  }
  else {
    while (read_line(line, MAX_LINE, fp)) {
      int k = 0;
      while (k < MAX_LINE) {
        if (line[k] == '\n' || line[k] == '\0') {
          line[k] = '\0';
          break;
        }
        k++;
      }
      ASSERT_ZERO(pthread_mutex_lock(outMutex));
      strcpy_m(dest, MAX_LINE, line);
      ASSERT_ZERO(pthread_mutex_unlock(tasks[task_id].outMutex));
    }
    int err = fclose(fp);
    if (err != 0 && err != 9) {
      syserr("fclose");
      exit(1);
    }
  }
  return NULL;
}

/* Main function of thread managing a task's error stream */
void* error_writer_main(void* data) {
  ASSERT_ZERO(pthread_mutex_lock(mutex));
  int task_id = *((int*) data);
  int fd = *(tasks[task_id].stderr_fd);
  FILE *fp;
  fp = fdopen(fd, "r");
  char line[MAX_LINE];
  char* dest = tasks[task_id].errBuffer;
  pthread_mutex_t *errMutex = tasks[task_id].errMutex;
  *(tasks[task_id].howMany) = *(tasks[task_id].howMany) + 1;
  if (*(tasks[task_id].howMany) == 2) {
    kill(*(tasks[task_id].pid), SIGUSR2);
    ASSERT_ZERO(pthread_cond_signal(cond));
  }
  ASSERT_ZERO(pthread_mutex_unlock(mutex));
  if (fp == NULL) {
    syserr("fdopen");
    exit(1);
  }
  else {
    while (read_line(line, MAX_LINE, fp)) {
      int k = 0;
      while (k < MAX_LINE) {
        if (line[k] == '\n' || line[k] == '\0') {
          line[k] = '\0';
          break;
        }
        k++;
      }
      ASSERT_ZERO(pthread_mutex_lock(errMutex));
      strcpy_m(dest, MAX_LINE, line);
      ASSERT_ZERO(pthread_mutex_unlock(errMutex));
    }
    int err = fclose(fp);
    if (err != 0 && err != 9) {
      syserr("fclose");
      exit(1);
    }
  }
  return NULL;
}

/* Function that mallocs space on the heap for all variables */
void init_variables() {
  working = malloc(sizeof(atomic_int));
  task_count = malloc(sizeof(atomic_int));
  *working = 1;
  *task_count = 0;
  q = malloc(sizeof(struct queue));
  init(q);
  mutex = malloc(sizeof(pthread_mutex_t));
  ASSERT_ZERO(pthread_mutex_init(mutex, NULL));
  cond = malloc(sizeof(pthread_cond_t));
  ASSERT_ZERO(pthread_cond_init(cond, NULL));
  signalsDone = malloc(sizeof(pthread_cond_t));
  ASSERT_ZERO(pthread_cond_init(signalsDone, NULL));
  signal_manager = malloc(sizeof(pthread_t));
}

/* Frees memory of all the global variables */
void free_variables() {
  for (int i = 0; i < *task_count; i++) {
    free(tasks[i].ended);
    ASSERT_ZERO(pthread_mutex_destroy(tasks[i].outMutex));
    ASSERT_ZERO(pthread_mutex_destroy(tasks[i].errMutex));
    free(tasks[i].outMutex);
    free(tasks[i].errMutex);
    free(tasks[i].outBuffer);
    free(tasks[i].errBuffer);
  }
  free(task_count);
  free(working);
  free(q);
  ASSERT_ZERO(pthread_mutex_destroy(mutex));
  free(mutex);
  ASSERT_ZERO(pthread_cond_destroy(cond));
  free(cond);
  ASSERT_ZERO(pthread_cond_destroy(signalsDone));
  free(signalsDone);
  free(signal_manager);
}

void init_task_data(pid_t pid, int task_id, int outfd, int errfd) {
  tasks[task_id].pid = malloc(sizeof(pid_t));
  *(tasks[task_id].pid) = pid;
  tasks[task_id].task_id = malloc(sizeof(int));
  *(tasks[task_id].task_id) = task_id;
  tasks[task_id].howMany = malloc(sizeof(int));
  *(tasks[task_id].howMany) = 0;
  tasks[task_id].stdout_fd = malloc(sizeof(int));
  *(tasks[task_id].stdout_fd) = outfd;
  tasks[task_id].stderr_fd = malloc(sizeof(int));
  *(tasks[task_id].stderr_fd) = errfd;
  tasks[task_id].ended = malloc(sizeof(int));
  *(tasks[task_id].ended) = 0;
  tasks[task_id].outBuffer = malloc(MAX_LINE * sizeof(char));
  tasks[task_id].errBuffer = malloc(MAX_LINE * sizeof(char));
  tasks[task_id].outBuffer[0] = '\0';
  tasks[task_id].errBuffer[0] = '\0';
  tasks[task_id].outMutex = malloc(sizeof(pthread_mutex_t));
  ASSERT_ZERO(pthread_mutex_init(tasks[task_id].outMutex, NULL));
  tasks[task_id].errMutex = malloc(sizeof(pthread_mutex_t));
  ASSERT_ZERO(pthread_mutex_init(tasks[task_id].errMutex, NULL));
  tasks[task_id].outThread = malloc(sizeof(pthread_t));
  tasks[task_id].errThread = malloc(sizeof(pthread_t));
  ASSERT_ZERO(pthread_create(tasks[task_id].outThread, NULL, output_writer_main, tasks[task_id].task_id));
  ASSERT_ZERO(pthread_create(tasks[task_id].errThread, NULL, error_writer_main, tasks[task_id].task_id));
}

/* Frees memory of a task, this function assumes it was called while holding mutex */
void free_task_data(int task_id) {
  pthread_t *out = tasks[task_id].outThread;
  pthread_t *err = tasks[task_id].errThread;
  ASSERT_ZERO(pthread_mutex_unlock(mutex));
  ASSERT_ZERO(pthread_join(*out, NULL));
  ASSERT_ZERO(pthread_join(*err, NULL));
  ASSERT_ZERO(pthread_mutex_lock(mutex));
  free(tasks[task_id].task_id);
  free(tasks[task_id].howMany);
  free(tasks[task_id].pid);
  tasks[task_id].pid = NULL;
  free(tasks[task_id].stderr_fd);
  free(tasks[task_id].stdout_fd);
  free(tasks[task_id].outThread);
  free(tasks[task_id].errThread);
}

/* Main function of signal managing thread */
void* signal_manager_main() {
  sigset_t set;
  int sig;
  ASSERT_ZERO(sigfillset(&set));
  while (true) {
    ASSERT_ZERO(sigwait(&set, &sig));
    if (sig == SIGUSR1) {
      int status;
      ASSERT_ZERO(pthread_mutex_lock(mutex));
      for (int i = 0; i < *task_count; i++) {
        if (!(*(tasks[i].ended))) {
          waitpid(*(tasks[i].pid), &status, 0);
          free_task_data(i);
          *(tasks[i].ended) = 1;
          printf("Task %d ended: ", i);
          if (WIFEXITED(status)) {
            printf("status %d.\n", WEXITSTATUS(status));
          } else {
            printf("signalled.\n");
          }
        }
      }
      ASSERT_ZERO(pthread_mutex_unlock(mutex));
      break;
    }
    else if (sig == SIGINT || sig == SIGKILL) {
      exit(1);
    }
    else if (sig == SIGCHLD) {
      int status;
      pid_t id = wait(&status);
      ASSERT_ZERO(pthread_mutex_lock(mutex));
      for (int i = 0; i < *task_count; i++) {
        if (!(*(tasks[i].ended)) && id == *(tasks[i].pid)) {
          if (!(*working)) free_task_data(i);
          *(tasks[i].ended) = 1;
          if (*(working)) {
            enqueue(q, i, status);
          }
          else {
            printf("Task %d ended: ", i);
            if (WIFEXITED(status)) {
              printf("status %d.\n", WEXITSTATUS(status));
            } else {
              printf("signalled.\n");
            }
          }
          break;
        }
      }
      ASSERT_ZERO(pthread_cond_signal(signalsDone));
      ASSERT_ZERO(pthread_mutex_unlock(mutex));
    }
  }
  return NULL;
}

/* Write the last line of a task's stdout to output */
void out_command(int task_id) {
  char line[MAX_LINE];
  ASSERT_ZERO(pthread_mutex_lock(mutex));
  char *src = tasks[task_id].outBuffer;
  pthread_mutex_t *readMutex = tasks[task_id].outMutex;
  ASSERT_ZERO(pthread_mutex_unlock(mutex));
  ASSERT_ZERO(pthread_mutex_lock(readMutex));
  strcpy_m(line, MAX_LINE, src);
  ASSERT_ZERO(pthread_mutex_unlock(readMutex));
  for (int i = 0; i < MAX_LINE; i++) {
    if (line[i] == '\n' || line[i] == '\0') {
      line[i] = '\0';
      break;
    }
  }
  ASSERT_ZERO(pthread_mutex_lock(mutex));
  printf("Task %d stdout: '%s'.\n", task_id, line);
  ASSERT_ZERO(pthread_mutex_unlock(mutex));
}

/* Write the last line of a task's stderr to output */
void err_command(int task_id) {
  char line[MAX_LINE];
  ASSERT_ZERO(pthread_mutex_lock(mutex));
  char *src = tasks[task_id].errBuffer;
  pthread_mutex_t *readMutex = tasks[task_id].errMutex;
  ASSERT_ZERO(pthread_mutex_unlock(mutex));
  ASSERT_ZERO(pthread_mutex_lock(readMutex));
  strcpy_m(line, MAX_LINE, src);
  ASSERT_ZERO(pthread_mutex_unlock(readMutex));
  for (int i = 0; i < MAX_LINE; i++) {
    if (line[i] == '\n' || line[i] == '\0') {
      line[i] = '\0';
      break;
    }
  }
  ASSERT_ZERO(pthread_mutex_lock(mutex));
  printf("Task %d stderr: '%s'.\n", task_id, line);
  ASSERT_ZERO(pthread_mutex_unlock(mutex));
}

/* Send SIGINT to child process */
void kill_command(int task_id) {
  ASSERT_ZERO(pthread_mutex_lock(mutex));
  if (tasks[task_id].pid != NULL) {
    pid_t pid = *(tasks[task_id].pid);
    kill(pid, SIGINT);
  }
  
  ASSERT_ZERO(pthread_mutex_unlock(mutex));
  
}

/* Pause main thread */
void sleep_command(int milliseconds) {
  __useconds_t time = (__useconds_t) milliseconds;
  usleep(time * 1000);
}

void run_command(char** args) {
  ASSERT_ZERO(pthread_mutex_lock(mutex));
  int task_id = *task_count;
  *task_count = *task_count + 1;
  tasks[task_id].ended = NULL;
  ASSERT_ZERO(pthread_mutex_unlock(mutex));

  int pipe1[2], pipe2[2];
  ASSERT_ZERO(pipe(pipe1));
  ASSERT_ZERO(pipe(pipe2));
  
  pid_t pid = fork();
  ASSERT_SYS_OK(pid);
  
  if (pid == 0) {
    // This is the child process.
    // Close the read end of the pipes
    ASSERT_SYS_OK(close(pipe1[0]));
    ASSERT_SYS_OK(close(pipe2[0]));

    sigset_t set;
    sigemptyset(&set);
    sigaddset(&set, SIGUSR2);
    
    int sig;

    sigwait(&set, &sig);

    // The child shouldn't block our signals
    sigset_t default_mask;
    sigemptyset(&default_mask);
    sigprocmask(SIG_SETMASK, &default_mask, NULL);

    // Reset signal handling dispositions to default
    for (int i = 1; i <= NSIG; i++) {
      signal(i, SIG_DFL);
    }

    // Connect the output and error streams to the write end of the pipes
    ASSERT_SYS_OK(dup2(pipe1[1], STDOUT_FILENO));
    ASSERT_SYS_OK(dup2(pipe2[1], STDERR_FILENO));

    // Close the write end of the pipes
    ASSERT_SYS_OK(close(pipe1[1]));
    ASSERT_SYS_OK(close(pipe2[1]));

    execvp(args[0], args);
    exit(EXIT_FAILURE);
  } else {
    // This is the parent process. Save the task information.
    ASSERT_SYS_OK(close(pipe1[1]));
    ASSERT_SYS_OK(close(pipe2[1]));
    init_task_data(pid, task_id, pipe1[0], pipe2[0]);
    ASSERT_ZERO(pthread_mutex_lock(mutex));
    while (*(tasks[task_id].howMany) != 2) ASSERT_ZERO(pthread_cond_wait(cond, mutex));
    printf("Task %d started: pid %d.\n", task_id, pid);
    ASSERT_ZERO(pthread_mutex_unlock(mutex));
  }
}

/* Ends the work of executor - signals unterminated processes and frees memory */
void quit_command() {
  ASSERT_ZERO(pthread_mutex_lock(mutex));
  while (!is_empty(q)) {
    int status;
    int id = dequeue(q, &status);
    free_task_data(id);
    printf("Task %d ended: ", id);
    if (WIFEXITED(status)) {
      printf("status %d.\n", WEXITSTATUS(status));
    } else {
      printf("signalled.\n");
    }
  }
  
  *working = 0;

  for (int i = 0; i < *task_count; i++) {
    if (*(tasks[i].ended) == 0) {
      kill(*(tasks[i].pid), SIGKILL);
    }
  }

  ASSERT_ZERO(pthread_kill(*signal_manager, SIGUSR1));
  ASSERT_ZERO(pthread_mutex_unlock(mutex));
  ASSERT_ZERO(pthread_join(*signal_manager, NULL));
  free_variables();
  exit(EXIT_SUCCESS);
}


/* Removes any extra spaces and newline from a line of input */
char* prepare_line(char* line, int len) {
  char* line2 = malloc(len * sizeof(char));
  int i = 0; int j = 0;
  bool wasSpace = true;
  while (i < len) {
    if (wasSpace) {
      if (line[i] == ' ') {
        i++;
      }
      else {
        wasSpace = false;
        if (line[i] == '\n' || line[i] == '\0') {
          line2[j] = '\0';
          break;
        } 
        line2[j] = line[i];
        i++;
        j++;
      }
    }
    else {
      if (line[i] == ' ') {
        wasSpace = true;
        line2[j] = line[i];
        i++;
        j++;
      }
      else {
        if (line[i] == '\n' || line[i] == '\0') {
          line2[j] = '\0';
          break;
        } 
        line2[j] = line[i];
        i++;
        j++;
      }
    }

  }
  if (j < len) line2[j] = '\0';
  else line2[len - 1] = '\0'; 
  free(line);
  return line2;
}

int main() {
  
    init_variables();
    
    sigset_t set;
    sigset_t signals;
    ASSERT_ZERO(sigfillset(&set));
    ASSERT_ZERO(pthread_sigmask(SIG_BLOCK, &set, NULL));
    ASSERT_ZERO(pthread_create(signal_manager, NULL, signal_manager_main, NULL));

    size_t len = 512;
    char *line = malloc(len * sizeof(char));

    while (read_line(line, len, stdin)) {
        ASSERT_ZERO(pthread_mutex_lock(mutex));
        *working = 1;
        ASSERT_ZERO(pthread_mutex_unlock(mutex));
        // Split the line into tokens.
        line = prepare_line(line, len);
        char** command = split_string(line);
        char* token = command[0];
        if (strcmp(token, "") == 0) {
            free_split_string(command);
            continue; // Ignore blank lines.
        }// Handle the command.
        if (strcmp(token, "run") == 0) {
            // Get the program name and arguments.
            char** args = command + 1;
            run_command(args);
        } else if (strcmp(token, "out") == 0) {
            // Get the task id.
            int task_id = atoi(command[1]);
            out_command(task_id);
        } else if (strcmp(token, "err") == 0) {
            // Get the task id.
            int task_id = atoi(command[1]);
            err_command(task_id);
        } else if (strcmp(token, "kill") == 0) {
            // Get the task id.
            int task_id = atoi(command[1]);
            kill_command(task_id);
        } else if (strcmp(token, "sleep") == 0) {
            // Get the sleep duration.
            int milliseconds = atoi(command[1]);
            sleep_command(milliseconds);
        } else if (strcmp(token, "quit") == 0) {
            free(line);
            quit_command();
        }
        free_split_string(command);
        ASSERT_ZERO(pthread_mutex_lock(mutex));
        sigpending(&signals);
        while (sigismember(&signals, SIGCHLD)) {
          ASSERT_ZERO(pthread_cond_wait(signalsDone, mutex));
          sigpending(&signals);
        }
        // Check if any tasks have ended.
        while (!is_empty(q)) {
            int status;
            int id = dequeue(q, &status);
            free_task_data(id);
            printf("Task %d ended: ", id);
            if (WIFEXITED(status)) {
              printf("status %d.\n", WEXITSTATUS(status));
            } else {
              printf("signalled.\n");
            }
        }
        *working = 0;
        ASSERT_ZERO(pthread_mutex_unlock(mutex));
    }
    free(line);
    quit_command();

    return EXIT_SUCCESS;
}