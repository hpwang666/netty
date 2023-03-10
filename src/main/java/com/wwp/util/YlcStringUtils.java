package com.wwp.util;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.stream.IntStream;

public class YlcStringUtils {
    static short seq=0;


    public static String bcd2string(short[] src)
    {
        StringBuffer sb = new StringBuffer ();
        for (int i = 0;i < src.length;i++ )
        {
            int h = ((src [i] & 0x00ff) >> 4) + 48;
            sb.append ((char) h);
            int l = (src [i] & 0x000f) + 48;
            sb.append ((char) l);
        }
        return sb.toString();
    }


    public static String bcd2string(short[] src,int length)
    {
        StringBuffer sb = new StringBuffer ();
        for (int i = 0;i < length;i++ )
        {
            int h = ((src [i] & 0x00ff) >> 4) + 48;
            sb.append ((char) h);
            int l = (src [i] & 0x000f) + 48;
            sb.append ((char) l);
        }
        return sb.toString();
    }

    public static byte[] string2bcd(String s)
    {

        if(s.length () % 2 != 0)
        {
            s = "0" + s;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        char [] cs = s.toCharArray ();
        for (int i = 0;i < cs.length;i += 2)
        {
            int high = cs [i] - 48;
            int low = cs [i + 1] - 48;
            baos.write (high << 4 | low);
        }
        return baos.toByteArray ();
    }

    public static String parseByte2HexStr(byte[] buf) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    public static String parseByte2HexStr(short[] buf) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 16????????????????????????
     *
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }


//    public static String IntsEncodeB64(int[] src,int length)
//    {
//        ByteBuffer buf = ByteBuffer.allocate(length);
//        IntStream.of(src).forEach(i -> buf.put((byte)i));
//        return Base64.getEncoder().encodeToString(buf.array());
//    }
//
//    public static byte[] decodeBytes(String b64)
//    {
//        return Base64.getDecoder().decode(b64);
//    }

    public static Date cp56Time2Date(short[] cp56Time)
    {
        Date date=null;
        try {
            if (cp56Time.length < 7) throw new ParseException("too short ", 0);
            String year = "20" + (cp56Time[6] & 0x7f);
            String month = String.valueOf(cp56Time[5] & 0x0f);
            String day = String.valueOf(cp56Time[4] & 0x1f);

            String hour = String.valueOf(cp56Time[3] & 0x1f);
            String min = String.valueOf(cp56Time[2] & 0x3f);

            int sec = ((cp56Time[1] & 0xff) << 8 | (cp56Time[0])) / 1000;


            String dateStr = year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec;
            String strDateFormat = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
            date =  sdf.parse(dateStr);
        }
        catch (ParseException e){
             e.printStackTrace();
        }
        return  date;
    }

    public static byte[] Date2cp56Time()
    {
        byte[] cp56Time = new byte[7];
        try{
            Calendar calendar = Calendar.getInstance();
            System.out.println("???????????????"+calendar.get(Calendar.YEAR));
            System.out.println("???????????????"+calendar.get(Calendar.MONTH));// 0 - 11
            System.out.println("????????????"+calendar.get(Calendar.DAY_OF_MONTH));
            System.out.println("????????????"+calendar.get(Calendar.DAY_OF_WEEK));
            System.out.println("????????????"+calendar.get(Calendar.HOUR_OF_DAY));
            System.out.println("????????????"+calendar.get(Calendar.MINUTE));
            System.out.println("????????????"+calendar.get(Calendar.SECOND));


            Integer year =calendar.get(Calendar.YEAR);
            Integer month =calendar.get(Calendar.MONTH);
            Integer day =calendar.get(Calendar.DAY_OF_MONTH);
            Integer week = calendar.get(Calendar.DAY_OF_WEEK);
            Integer hour =calendar.get(Calendar.HOUR_OF_DAY);
            Integer min =calendar.get(Calendar.MINUTE);
            Integer sec =calendar.get(Calendar.SECOND);



            cp56Time[0] = (byte)((sec*1000)&0xff);
            cp56Time[1] = (byte)(((sec*1000)>>8)&0xff);
            cp56Time[2] = (byte)(min&0x3f);
            cp56Time[3] = (byte)(hour&0x1f);
            cp56Time[4] = (byte)((week&0xc0)|(day&0x3f));
            cp56Time[5] = (byte)((month+1)&0x0f);
            cp56Time[6] = (byte)((year-2000)&0x7f);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return cp56Time;
    }


   // CRC ??????????????????????????????????????????

    public static int crc(short[]  pData,int length)
    {
        int byCRCHi = 0xff;
        int byCRCLo = 0xff;
        int byIdx;
        int crc;

        for (int i = 0;i < length;i++ ) {
            byIdx = (byCRCHi ^ pData[i])&0xff;
            byCRCHi = (byCRCLo ^ gabyCRCHi[byIdx])&0xff;
            byCRCLo = gabyCRCLo[byIdx]&0xff;
        }
        crc = byCRCHi&0xff;
        crc <<= 8;
        crc += byCRCLo&0xff;
        return crc;
    }

    public static int crc(byte[]  pData,int length)
    {
        int byCRCHi = 0xff;
        int byCRCLo = 0xff;
        int byIdx;
        int crc;

        for (int i = 0;i < length;i++ ) {
            byIdx = (byCRCHi ^ pData[i])&0xff;
            byCRCHi = (byCRCLo ^ gabyCRCHi[byIdx])&0xff;
            byCRCLo = gabyCRCLo[byIdx]&0xff;
        }
        crc = byCRCHi&0xff;
        crc <<= 8;
        crc += byCRCLo&0xff;
        return crc;
    }
    //CRC ???????????????
    static final short[] gabyCRCHi =
            {
                    0x00,0xc1,0x81,0x40,0x01,0xc0,0x80,0x41,0x01,0xc0,
                    0x80,0x41,0x00,0xc1,0x81,0x40,0x01,0xc0,0x80,0x41,
                    0x00,0xc1,0x81,0x40,0x00,0xc1,0x81,0x40,0x01,0xc0,
                    0x80,0x41,0x01,0xc0,0x80,0x41,0x00,0xc1,0x81,0x40,
                    0x00,0xc1,0x81,0x40,0x01,0xc0,0x80,0x41,0x00,0xc1,
                    0x81,0x40,0x01,0xc0,0x80,0x41,0x01,0xc0,0x80,0x41,
                    0x00,0xc1,0x81,0x40,0x01,0xc0,0x80,0x41,0x00,0xc1,
                    0x81,0x40,0x00,0xc1,0x81,0x40,0x01,0xc0,0x80,0x41,
                    0x00,0xc1,0x81,0x40,0x01,0xc0,0x80,0x41,0x01,0xc0,
                    0x80,0x41,0x00,0xc1,0x81,0x40,0x00,0xc1,0x81,0x40,
                    0x01,0xc0,0x80,0x41,0x01,0xc0,0x80,0x41,0x00,0xc1,
                    0x81,0x40,0x01,0xc0,0x80,0x41,0x00,0xc1,0x81,0x40,
                    0x00,0xc1,0x81,0x40,0x01,0xc0,0x80,0x41,0x01,0xc0,
                    0x80,0x41,0x00,0xc1,0x81,0x40,0x00,0xc1,0x81,0x40,
                    0x01,0xc0,0x80,0x41,0x00,0xc1,0x81,0x40,0x01,0xc0,
                    0x80,0x41,0x01,0xc0,0x80,0x41,0x00,0xc1,0x81,0x40,
                    0x00,0xc1,0x81,0x40,0x01,0xc0,0x80,0x41,0x01,0xc0,
                    0x80,0x41,0x00,0xc1,0x81,0x40,0x01,0xc0,0x80,0x41,
                    0x00,0xc1,0x81,0x40,0x00,0xc1,0x81,0x40,0x01,0xc0,
                    0x80,0x41,0x00,0xc1,0x81,0x40,0x01,0xc0,0x80,0x41,
                    0x01,0xc0,0x80,0x41,0x00,0xc1,0x81,0x40,0x01,0xc0,
                    0x80,0x41,0x00,0xc1,0x81,0x40,0x00,0xc1,0x81,0x40,
                    0x01,0xc0,0x80,0x41,0x01,0xc0,0x80,0x41,0x00,0xc1,
                    0x81,0x40,0x00,0xc1,0x81,0x40,0x01,0xc0,0x80,0x41,
                    0x00,0xc1,0x81,0x40,0x01,0xc0,0x80,0x41,0x01,0xc0,
                    0x80,0x41,0x00,0xc1,0x81,0x40
            };
   // CRC ???????????????
   static final short[] gabyCRCLo =
            {
                    0x00,0xc0,0xc1,0x01,0xc3,0x03,0x02,0xc2,0xc6,0x06,
                    0x07,0xc7,0x05,0xc5,0xc4,0x04,0xcc,0x0c,0x0d,0xcd,
                    0x0f,0xcf,0xce,0x0e,0x0a,0xca,0xcb,0x0b,0xc9,0x09,
                    0x08,0xc8,0xd8,0x18,0x19,0xd9,0x1b,0xdb,0xda,0x1a,
                    0x1e,0xde,0xdf,0x1f,0xdd,0x1d,0x1c,0xdc,0x14,0xd4,
                    0xd5,0x15,0xd7,0x17,0x16,0xd6,0xd2,0x12,0x13,0xd3,
                    0x11,0xd1,0xd0,0x10,0xf0,0x30,0x31,0xf1,0x33,0xf3,
                    0xf2,0x32,0x36,0xf6,0xf7,0x37,0xf5,0x35,0x34,0xf4,
                    0x3c,0xfc,0xfd,0x3d,0xff,0x3f,0x3e,0xfe,0xfa,0x3a,
                    0x3b,0xfb,0x39,0xf9,0xf8,0x40,0x28,0xe8,0xe9,0x29,
                    0xeb,0x2b,0x2a,0xea,0xee,0x2e,0x2f,0xef,0x2d,0xed,
                    0xec,0x2c,0xe4,0x24,0x25,0xe5,0x27,0xe7,0xe6,0x26,
                    0x22,0xe2,0xe3,0x23,0xe1,0x21,0x20,0xe0,0xa0,0x60,
                    0x61,0xa1,0x63,0xa3,0xa2,0x62,0x66,0xa6,0xa7,0x67,
                    0xa5,0x65,0x64,0xa4,0x6c,0xac,0xad,0x6d,0xaf,0x6f,
                    0x6e,0xae,0xaa,0x6a,0x6b,0xab,0x69,0xa9,0xa8,0x68,
                    0x78,0xb8,0xb9,0x79,0xbb,0x7b,0x7a,0xba,0xbe,0x7e,
                    0x7f,0xbf,0x7d,0xbd,0xbc,0x7c,0xb4,0x74,0x75,0xb5,
                    0x77,0xb7,0xb6,0x76,0x72,0xb2,0xb3,0x73,0xb1,0x71,
                    0x70,0xb0,0x50,0x90,0x91,0x51,0x93,0x53,0x52,0x92,
                    0x96,0x56,0x57,0x97,0x55,0x95,0x94,0x54,0x9c,0x5c,
                    0x5d,0x9d,0x5f,0x9f,0x9e,0x5e,0x5a,0x9a,0x9b,0x5b,
                    0x99,0x59,0x58,0x98,0x88,0x48,0x49,0x89,0x4b,0x8b,
                    0x8a,0x4a,0x4e,0x8e,0x8f,0x4f,0x8d,0x4d,0x4c,0x8c,
                    0x44,0x84,0x85,0x45,0x87,0x47,0x46,0x86,0x82,0x42,
                    0x43,0x83,0x41,0x81,0x80,0x40
            };


    static public String genOrderNum(String serialId, Integer plugNo)
    {
        StringBuffer sb = new StringBuffer ();
        sb.append(serialId);
        sb.append(String.format("%02d", plugNo));

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        String str = sdf.format(date);

        sb.append(str);
        seq++;
        if(seq==9999) seq =0;
        sb.append(String.format("%04d", seq));
        return sb.toString();
    }

    static public String genLogicalNum()
    {
        char[] chars = "0102030405060708090123456789".toCharArray();
        int length = chars.length;

        StringBuffer sb = new StringBuffer ();
        sb.append("5800");
        for (int i = 0; i < 12; i++){
            char achar = chars[new Random().nextInt(length)];
            sb.append(achar);
        }

        return sb.toString();
    }
}



