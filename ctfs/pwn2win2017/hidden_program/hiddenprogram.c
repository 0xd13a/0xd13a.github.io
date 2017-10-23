#include <stdio.h>
#include <limits.h>
#include <string.h>

typedef struct
{
    char flag[SHRT_MAX+1];
    char in[SHRT_MAX+1];
    char sub[SHRT_MAX+1];
    int n;
} player;

player p1;

void main()
{    
    FILE *fp = fopen("/home/hidden-program/flag","r");
    memset(p1.flag,0,sizeof(p1.flag));
    fscanf(fp,"%[^\n]",p1.flag);
    fclose(fp);
    while(1)
    {
        printf("Insert a short integer: ");
        fflush(stdout);
        scanf(" %d", &p1.n);
        if(p1.n>SHRT_MAX)
            printf("Invalid number\n\n");
        else break;
    }
    p1.n = (short)abs((short)p1.n);
    printf("Insert a string: ");
    fflush(stdout);
    scanf("%10000s",p1.in);
    printf("Insert another string: ");
    fflush(stdout);
    scanf("%10000s",p1.sub);
    if(strcmp(&p1.in[p1.n],p1.sub)==0) printf("Congratulations!! YOU WIN!!\n");
    else
        printf("\tYou lost!!!\n\
        In the string %s the substring in the position %d is %s\n\
        Try again...\n", p1.in, p1.n, &p1.in[p1.n]);
    fflush(stdout);
}
