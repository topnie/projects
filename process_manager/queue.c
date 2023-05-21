#include "queue.h"
#include <stdio.h>
#include <stdlib.h>

/* Initialize the queue */
void init(struct queue *q) {
  q->head = 0;
  q->tail = 0;
}

/* Return 1 if the queue is empty, 0 otherwise */
int is_empty(struct queue *q) {
  return q->head == q->tail;
}

/* Return 1 if the queue is full, 0 otherwise */
int is_full(struct queue *q) {
  return (q->tail + 1) % BUFFER_SIZE == q->head;
}

/* Add an element to the tail of the queue */
void enqueue(struct queue *q, int id, int status) {
  if (is_full(q)) {
    fprintf(stderr, "Error: queue is full\n");
    exit(EXIT_FAILURE);
  }
  q->statusData[q->tail] = status;
  q->taskId[q->tail] = id;
  q->tail = (q->tail + 1) % BUFFER_SIZE;
}

/* Remove and return the element at the head of the queue */
int dequeue(struct queue *q, int *status) {
  if (is_empty(q)) {
    fprintf(stderr, "Error: queue is empty\n");
    exit(EXIT_FAILURE);
  }
  int x = q->taskId[q->head];
  *status = q->statusData[q->head];
  q->head = (q->head + 1) % BUFFER_SIZE;
  return x;
}
