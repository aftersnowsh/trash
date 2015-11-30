#include <stdio.h>
#include <string.h>
#include <locale.h>

#define FALSE 0
#define TRUE 1

extern int utf_ex(char *m_code);

int code_index(char c)
{
    int num=0;

    switch(c)
    {
        case 'a':
        case 'A':
            num = 10;
            break;
        case 'b':
        case 'B':
            num = 11;
            break;
        case 'c':
        case 'C':
            num = 12;
            break;
        case 'd':
        case 'D':
            num = 13;
            break;
        case 'e':
        case 'E':
            num = 14;
            break;
        case 'f':
        case 'F':
            num = 15;
            break;
        default:
            num = atoi(&c);
            break;
    }

    return num;
}

int enc_utf(char *str)
{
    unsigned int upr_num;
    unsigned int low_num;
    char code[10];
    char c;
    int rslt = FALSE;
    int rslt_ex = FALSE;

    strncpy(code, str, 8);
    code[9] = 0 ;

    low_num = code_index(code[1]);
    upr_num = code_index(code[0]);

    if(8 > upr_num)
    {
        //0x00 - 0x70までは１バイト文字
        c = upr_num << 4;
        c += low_num;
        printf("%c", c);
        rslt = TRUE;
    }
    else if(0xe == upr_num)
    {
        //0xExからは３バイト文字
        if(6 <= strlen(code))
        {
            rslt_ex = utf_ex(code);
            if(rslt_ex != TRUE)
            {
                printf("[%s]",str);
            }
            rslt = TRUE;
        }
        else
        {
            rslt = FALSE;
        }
    }
    else
    {
        //他はとりあえず
        printf("[%s]",str);
        rslt = TRUE;
    }

    return rslt;
}

int main(int argc, char *argv[])
{
    FILE *fp;
    char c;
    char enc[100];
    int enc_flg = FALSE;
    int rslt = FALSE;
    int moji_cnt=0;
    int i=0;

    if(2 > argc)
    {
        return -1;
    }
    printf("%s\n",argv[1]);
        
    fp = fopen(argv[1], "r");
    if(NULL == fp)
    {
        printf("fopen失敗\n");
        return -1;
    }

    memset(enc,0x00,sizeof(enc));

    while( (c = fgetc( fp )) != EOF )
    {
        if(TRUE == enc_flg)
        {
            if( 0 < moji_cnt)
            {
                if('%' != c)
                {
                    enc[i] = c;
                    moji_cnt--;
                    i++;
                }
            }
            else
            {
                rslt = enc_utf(enc);
                enc_flg = FALSE;
                if(FALSE == rslt)
                {
                    //マルチコードの場合は
                    //エンコード継続
                    moji_cnt = 2;
                }
                else
                {
                    enc_flg = FALSE;
                    i = 0;
                    memset(enc,0x00,sizeof(enc));
                }
            }
        }

        if('%' == c)
        {
            if(TRUE != enc_flg)
            {
                enc_flg = TRUE;
                moji_cnt = 2;
            }
        }
        else
        {
           if(TRUE != enc_flg)
           {
               printf( "%c", c );
           }
        }
    }
    printf( "\n");

    fclose( fp );
    return 0;
} 
