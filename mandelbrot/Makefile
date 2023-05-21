NASM=nasm
GCC=gcc

NASMFLAGS=-f elf64 -g -w+all -w+error
GCCFLAGS=-c -Wall -Wextra -std=c17 -O2 -z noexecstack
LDFLAGS=-lglfw -lGL -lGLU -lglut

all: display

mandelbrot.o: mandelbrot.asm
	$(NASM) $(NASMFLAGS) -o $@ $<

display.o: display.c
	$(GCC) $(GCCFLAGS) -o $@ $<

display: display.o mandelbrot.o
ifeq ($(shell pkg-config --exists glfw3 && echo yes),yes)
ifeq ($(shell pkg-config --exists gl && echo yes),yes)
	$(GCC) $(LDFLAGS) -o $@ $^ `pkg-config --libs glfw3` -lGL -lGLU -lglut
else
	$(error "Error: OpenGL library is not installed. Please install it using your system's package manager.")
endif
else
	$(error "Error: glfw3 library is not installed. Please install it using your system's package manager.")
endif

clean:
	rm -f display display.o mandelbrot.o
