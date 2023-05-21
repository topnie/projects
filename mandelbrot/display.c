#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <malloc.h>
#include <GLFW/glfw3.h> // Biblioteka od wyświetlania okien.
#include <GL/glut.h> // OpenGL żeby wyświetlać bitmapę w oknie.

// Funkcja mandelbrot przyjmuje bitmapę z 4 bajtami na pixel (BRGA) i koordynatami wierzchołków,
// generuje zbiór mandelbrota w takim prostokącie.
void mandelbrot(GLubyte* image, int width, int height, double x0, double x1, double y0, double y1);

// Stała globalna mówiąca, czy użytkownik wcisnął jakiś przycisk.
int changed = 1;

// Struktura koordynatów prostokąta.
typedef struct {
    float x0;
    float y0;
    float x1;
    float y1;
} Rect;

// Globalny prostokąt, aktualny stan okna.
Rect rect = {-1.0f, -1.0f, 1.0f, 1.0f};

// Funkcja rejestrująca przyciski klawiatury.
void key_callback(GLFWwindow* window, int key, int scancode, int action, int mods)
{
    (void) window;
    (void) mods;
    (void) scancode;
    if (action == GLFW_PRESS || action == GLFW_REPEAT)
    {   
        // Zostały przyciśnietę przyciski klawiatury, zmieniamy koordynaty naszego prostokąta.
        changed = 1;
        // Delta musi być proporcjonalna do naszego zooma, żeby nie iść zbyt szybko przy dużym zoomie.
        float delta = 0.05f * (rect.x1 - rect.x0);
        switch (key)
        {
            case GLFW_KEY_UP:
                rect.y0 += delta;
                rect.y1 += delta;
                break;
            case GLFW_KEY_DOWN:
                rect.y0 -= delta;
                rect.y1 -= delta;
                break;
            case GLFW_KEY_LEFT:
                rect.x0 -= delta;
                rect.x1 -= delta;
                break;
            case GLFW_KEY_RIGHT:
                rect.x0 += delta;
                rect.x1 += delta;
                break;
        }
    }
}

// Funkcja rejestrująca scroll.
void scroll_callback(GLFWwindow* window, double xoffset, double yoffset)
{
    (void) window;
    (void) xoffset;
    float factor = 1.1f;
    float ysize = (rect.y1 - rect.y0) / 2.0;
    float xsize = (rect.x1 - rect.x0) / 2.0;
    float middlex = rect.x0 + xsize;
    float middley = rect.y0 + ysize;
    if (yoffset > 0)
    {
        xsize /= factor;
        ysize /= factor;
        
    }
    else if (yoffset < 0)
    {
        xsize *= factor;
        ysize *= factor;
    }
    rect.x0 = middlex - xsize;
    rect.y0 = middley - ysize;
    rect.x1 = middlex + xsize;
    rect.y1 = middley + ysize;
    changed = 1;
}


//Funkcja używająca OpenGL, żeby wyświetlić bitmapę w oknie.
void display_bitmap(GLubyte* bitmap, int width, int height)
{
 // Tworzymy teksturę 2D.
 GLuint texture;
 glGenTextures(1, &texture);
 glBindTexture(GL_TEXTURE_2D, texture);

 // Ustawiamy parametry tekstury.
 glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
 glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

 // Ustawiamy opcję przechowywania pikseli.
 glPixelStorei(GL_UNPACK_ALIGNMENT, 4);
 glPixelStorei(GL_UNPACK_ROW_LENGTH, width);
 glPixelStorei(GL_UNPACK_SKIP_PIXELS, 0);
 glPixelStorei(GL_UNPACK_SKIP_ROWS, 0);
 glPixelStorei(GL_UNPACK_SWAP_BYTES, GL_TRUE);

 // Wprowadzamy naszą bitmapę do obiektu tekstury.
 glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_BGRA, GL_UNSIGNED_BYTE, bitmap);

 glEnable(GL_TEXTURE_2D);

 // Rysujemy kwadrat z naszą teksturą w całym oknie.
 glBegin(GL_QUADS);
 glTexCoord2f(0.0f, 0.0f); glVertex2f(-1.0f, -1.0f);
 glTexCoord2f(1.0f, 0.0f); glVertex2f(1.0f, -1.0f);
 glTexCoord2f(1.0f, 1.0f); glVertex2f(1.0f, 1.0f);
 glTexCoord2f(0.0f, 1.0f); glVertex2f(-1.0f, 1.0f);
 glEnd();

 // Usuwamy teksturę.
 glDeleteTextures(1, &texture);
}



int main(int argc, char *argv[])
{
    // Sprawdzamy czy podano wielkość okna.
    if (argc != 3)
    {
        printf("Usage: %s width height\n", argv[0]);
        return 1;
    }

    // Parsujemy wejście.
    char *endptr;
    int width = strtol(argv[1], &endptr, 10);
    if (*endptr != '\0' || width < 0)
    {
        printf("Invalid width: %s\n", argv[1]);
        return 2;
    }
    int height = strtol(argv[2], &endptr, 10);
    if (*endptr != '\0' || height < 0)
    {
        printf("Invalid height: %s\n", argv[2]);
        return 3;
    }

    // Jeśli okno nie jest kwadratem, to musimy przeskalować nasz globalny prostokąt.
    if (width > height) {
        float factor = ((float) width) / ((float) height);
        rect.x0 *= factor;
        rect.x1 += factor;
    }
    else if (height > width) {
        float factor = ((float) height) / ((float) width);
        rect.y0 *= factor;
        rect.y1 += factor;
    }

    // Inicjalizacja GLFW
    if (!glfwInit())
    {
        return -1;
    }

    // Tworzymy okno i jego kontekst OpenGL
    GLFWwindow* window = glfwCreateWindow(width, height, "Mandelbrot Set", NULL, NULL);
    if (!window)
    {
        glfwTerminate();
        return -1;
    }

    // Rejestrujemy funkcje zajmujące się inputem.
    glfwSetKeyCallback(window, key_callback);
    glfwSetScrollCallback(window, scroll_callback);

    // Kontekst naszego okna jest aktywny.
    glfwMakeContextCurrent(window);

    // Alokuję potrzebną bitmapę.
    GLubyte* bitmap = (GLubyte*)malloc(height * width * 4);

    // Wyświetlamy obraz aż do końca programu.
    while (!glfwWindowShouldClose(window))
    {   
        // Jeśli użytkownik zmienił pozycję okna, to musimy generować mapę od nowa. 
        if (changed) {
            mandelbrot(bitmap, width, height, rect.x0, rect.x1, rect.y0, rect.y1);
            changed = 0;
        }
        display_bitmap(bitmap, width, height);
        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    // Zwalniamy pamięć.
    free(bitmap);

    // Zamykamy okno.
    glfwTerminate();
    return 0;
}