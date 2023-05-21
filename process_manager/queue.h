#ifndef __QUEUE__
#define __QUEUE__

#include <stdio.h>
#include <stdlib.h>

#define MAX_LINE 1024
#define BUFFER_SIZE 4096


/* The queue data structure */
struct queue {
  int taskId[BUFFER_SIZE];
  int statusData[BUFFER_SIZE];
  int head;
  int tail;
};

/* Initialize the queue */
void init(struct queue *q);

/* Return 1 if the queue is empty, 0 otherwise */
int is_empty(struct queue *q);

/* Return 1 if the queue is full, 0 otherwise */
int is_full(struct queue *q);

/* Add an element to the tail of the queue */
void enqueue(struct queue *q, int id, int status);

/* Remove and return the element at the head of the queue */
int dequeue(struct queue *q, int *status);

#endif