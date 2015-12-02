
import java.io.*;
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.InputStreamReader;
import java.util.*;

public class Gbr_decode {
    public static void main(String[] args) {
    
        if(0 >= args.length) {
            //引数がない場合は終了
            System.exit(1);
        }
        String FileName = args[0];
    
        // ファイルオブジェクトの生成
        File inputFile = new File(FileName);
        try {
            // 入力ストリームの生成
            FileInputStream fis = new FileInputStream(inputFile);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            // テキストファイルからの読み込み
            int c;
            int moji_cnt=0;
            int i=0;
            int[] m_code = new int[10];
            boolean rslt=false;
            boolean enc_flg = false;
            while((c = br.read()) != -1) {
                //System.out.print((char)c);

                if(true == enc_flg) {
                    if( 0 < moji_cnt) {
                        if('%' != (char)c)
                        {
                            m_code[i] = c;
                            moji_cnt--;
                            i++;
                        }
                    }
                    else {
                        rslt = enc_utf(m_code);
                        enc_flg = false;
                        if(false == rslt) {
                            //マルチコードの場合は
                            //エンコード継続
                            moji_cnt = 2;
                        }
                        else {
                            enc_flg = false;
                            i = 0;
                            Arrays.fill(m_code,0x00);
                        }
                    }
                }
        
                if('%' == (char)c) {
                    if(true != enc_flg) {
                        enc_flg = true;
                        moji_cnt = 2;
                    }
                }
                else {
                   if(true != enc_flg) {
                       System.out.print((char)c);
                   }
                }

            }
            System.out.println();
         
            // 後始末
            br.close();
            // エラーがあった場合は、スタックトレースを出力
        } 
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean enc_utf(int[] code) throws IOException {
        int upr_code, low_code;
        int work_code;

        upr_code = Integer.parseInt(String.valueOf((char)code[0]),16);
        low_code = Integer.parseInt(String.valueOf((char)code[1]),16);
        //System.out.print("["+upr_code+"|");
        //System.out.print(low_code+"]");

        if( 8 > upr_code) {
            work_code = upr_code << 4;
            work_code = work_code + low_code;
            System.out.print((char)work_code);
        }
        else if(0xe == upr_code) {
            //System.out.print("[["+(char)code[0]+"]]");
            int i;
            for(i=0; i<=6; i++) {
                if(0x00 == code[i]) {
                   break;
                }
            }

            if(6 <= i) {
                byte[] code_byte = new byte[4];

                work_code = upr_code << 4;
                work_code += low_code;
                code_byte[0] = (byte)work_code;
                //System.out.print("[["+(int)code_byte[0]+"|"+work_code+"]]");

                work_code = Integer.parseInt(String.valueOf((char)code[2]),16) << 4;
                work_code += Integer.parseInt(String.valueOf((char)code[3]),16);
                code_byte[1] = (byte)work_code;
                //System.out.print("[["+work_code+"]]");

                work_code = Integer.parseInt(String.valueOf((char)code[4]),16) << 4;
                work_code += Integer.parseInt(String.valueOf((char)code[5]),16);
                code_byte[2] = (byte)work_code;
                //System.out.print("[["+code_byte[3]+"]]");

                String str_utf = new String(code_byte, "UTF-8");
                System.out.print(str_utf);
            }
            else {
                return false;
            }
        }
        else {
            System.out.print("["+(char)code[0]+"]");
        }
        return true;
    }
}

