global mandelbrot ; funkcja generująca bitmapę BGRA zbioru mandelbrota na danym prostokącie

    section .data

two: dq 2.0 ; stała równa dwa, potrzebna przy sprawdzaniu wielkości numeru

    section .text

mandelbrot: ;rdi - wskaźnik do bitmapy, rsi - szerokość, rdx - wyoskość, xmm0 - x0, xmm1 - x1, xmm2 - y0, xmm3 - y1
    subsd xmm1, xmm0
    movsd xmm5, xmm1 ; xmm5 - rzeczywista szerokość prostokąta
    movsd xmm4, xmm0 ; xmm4 - x0
    subsd xmm3, xmm2
    movsd xmm7, xmm3 ; xmm7 - rzeczywista wysokość prostokąta
    movsd xmm6, xmm2 ; xmm6 - y0
    mov ecx, esi
    imul ecx, edx ; w ecx jest teraz ilośc pikseli (width * height)
    mov r8, rdx ; wysokość trzymamy w r8, szerokość w rsi
    xor r9, r9 ; zerujemy r9
    main_loop:
    xor rdx, rdx ; zerujemy rdx
    mov rax, r9 ; ładujemy numer aktualnego piksela do rax
    div rsi ; dzielimy numer piksela przez szerokość mapy, w rax - y (quotient), a w rdx, - x (remainder)
    cvtsi2sd xmm0, rdx ; zmieniamy rax na double
    cvtsi2sd xmm1, rsi ; rsi tak samo
    divsd xmm0, xmm1 ; dzielimy x-ową składową piksela przez szerokość w pikselach (otrzymujemy ułamek od 0 do 1)
    mulsd xmm0, xmm5 ; mnożymy ją przez rzeczywistą szerokość ramki
    addsd xmm0, xmm4 ; dodajemy offset (x0)
    cvtsi2sd xmm1, rax ; tak samo robimy z y-ową składową i wysokością ramki w pikselach
    cvtsi2sd xmm2, r8
    divsd xmm1, xmm2
    mulsd xmm1, xmm7
    addsd xmm1, xmm6 ; trzymamy x w xmm0 i y w xmm1, c = x + iy
    movsd xmm2, xmm0
    movsd xmm3, xmm1 ; będziemy trzymać Zn w xmm2 i xmm3.
    push rcx ; stos powinien być 16 byte aligned
    sub rsp, 16 ; zapisuje zmienne xmm4,5,6,7 na stosie, bo podczas kalkulacji używamy tych rejestrów
    movdqu [rsp], xmm4
    sub rsp, 16
    movdqu [rsp], xmm5
    sub rsp, 16
    movdqu [rsp], xmm6 
    sub rsp, 16 
    movdqu [rsp], xmm7 
    jmp calculate ; skaczę do kalkulacji piksela, wynik mam w rax, a w r10 ilość pozostałych iteracji
    write_result:
    movdqu xmm7, [rsp] ; przywracam ze stosu wartośći xmm7,6,5,4
    add rsp, 16
    movdqu xmm6, [rsp]
    add rsp, 16
    movdqu xmm5, [rsp]
    add rsp, 16 
    movdqu xmm4, [rsp]
    add rsp, 16
    mov rcx, r10 ; zapisuję ilość pozostałych iteracji w cl
    imul rcx, rcx, 8 ; mnożę ilość pozostałych iteracji razy 8 (bo 8 * 30 = 240 ~ 255)
    mov byte [rdi], al ; kanał niebieski jest 0, jeśli piksel jest w zbiorze i 255 wpp
    mov byte [rdi + 1], cl ; kanały czerwone i zielone są jaśniejsze im szybciej piksel wyskoczył poza limit wielkości
    mov byte [rdi + 2], cl
    mov byte [rdi + 3], 0xff ; kanał alpha jest stale równy 255
    pop rcx ; przywracam rcx ze stosu
    add rdi, 4 ; inkrementuję wskaźnik do bitmapy
    inc r9 ; inkrementuję aktualny piksel
    dec rcx ; dekrementuję ilość pozostałych pikseli
    cmp rcx, 0
    jne main_loop ; jeśli jeszcze jakieś są to kręcimy się dalej
    ret ; jak nie, to wychodzimy

calculate:
    mov r10, 30 ; ładujemy ilość iteracji jednego puktu w r10, tą stała można zmienić
    lea rax, [rel two] ; ładujemy 2.0 do xmm7
    movsd xmm7, [rax]

iteration:
    movsd xmm5, xmm2
    mulsd xmm5, xmm2 ; w xmm5 siedzi x^2
    movsd xmm6, xmm3
    mulsd xmm6, xmm3 ; w xmm6 siedzi y^2
    subsd xmm5, xmm6 ; teraz mamy Re(z) = x^2 - y^2
    mulsd xmm3, xmm2 ; mnożymy y razy x
    mulsd xmm3, xmm7 ; mamy Im(z) = 2xy
    movsd xmm2, xmm5 ; zapisujemy Re(z)
    addsd xmm2, xmm0 ; dodajemy C do z(n)
    addsd xmm3, xmm1 ; --
    movsd xmm5, xmm2 ; zapisujemy nowe wartości z(n+1) w 5 i 6
    movsd xmm6, xmm3
    mulsd xmm5, xmm2 ; liczymy moduł z(n+1)
    mulsd xmm6, xmm3
    addsd xmm5, xmm6
    sqrtsd xmm5, xmm5
    comisd xmm5, xmm7 ; jeśli z(n+1)[smm5] jest wiekszy niz 2.0, to jesteśmy poza zbiorem
    ja write_white ; kolorujemy piksel na biało
    dec r10
    cmp r10, 0
    je write_black ; jeśli przeszliśmy wszystkie iteracje, to kolorujemy na czarno
    jmp iteration ; wpp iterujemy się dalej

write_white:
    mov al, 0xFF
    jmp write_result

write_black:
    mov al, 0x00
    jmp write_result