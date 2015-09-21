package com.hylg.igolf.utils;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Base64 {
  static private String BaseString="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
  static private char[] BaseChars=BaseString.toCharArray();
  public Base64() {
  }
  static public String encode(byte[] source){
    StringBuffer ret=new StringBuffer("");
    int len=source.length;
    int knot=len/3;
    int i=0;
    int reg;
   for(;i<knot;i++){
      reg=i*3;
      ret.append(BaseChars[(source[reg]&0x0fc)>>>2])
          .append(BaseChars[((source[reg]&0x03)<<4)+((source[reg+1]&0x00f0)>>>4)])
          .append(BaseChars[((source[reg+1]&0x0f)<<2)+((source[reg+2]&0x0c0)>>>6)])
          .append(BaseChars[source[reg+2]&0x03f]);
    }
    reg=i*3;
    if (reg<len){
      if (reg+1==len){
        //剩余1个字节
        ret.append(BaseChars[(source[reg]&0x0fc)>>>2])
            .append(BaseChars[(source[reg]&0x03)<<4]).append('=').append('=');
      }else{
        //剩余2个字节
        ret.append(BaseChars[(source[reg]&0x0fc)>>>2])
            .append(BaseChars[((source[reg]&0x03)<<4)+((source[reg+1]&0x0f0)>>>4)])
            .append(BaseChars[(source[reg+1]&0x0f)<<2]).append('=');
      }
    }
    return ret.toString();
  }
  static public byte[] decode(String encodedString){
    if (encodedString.length()<4){
      return null;
    }
    char[] source =encodedString.toCharArray();
    int knot=source.length/4;
    int len=knot*3;
    if (source[source.length-1]== '='){
      if (source[source.length-2]== '='){
        len-=2;
      }else{
        len--;
      }
    }
    int[] reg=new int[4];
    if (knot*4!=source.length){
      //格式不对
      return null;
    }
    byte[] ret=new byte[len];
    int i,j;
    for(i=0;i<knot;i++){
      for(j=0;j<4;j++){
        reg[j]=BaseString.indexOf(source[i*4+j]);
      }
      ret[i*3]=(byte)((reg[0]<<2)+((reg[1]&0x30)>>>4));
      if (reg[2]!=-1){
        ret[i*3+1]=(byte)(((reg[1]&0x0f)<<4)+((reg[2]&0x3c)>>>2));
      }
      if (reg[3]!=-1){
        ret[i*3+2]=(byte)(((reg[2]&0x03)<<6)+reg[3]);
      }

    }
    return ret;
  }
//  public static void main(String[] args) {
//    Base64 base641 = new Base64();
//    byte[] b="9292164".getBytes();
//    System.out.println(encode(b));
//    
//    
//    byte[] ret=decode(encode(b));
//    System.out.println(new String(ret));
//    System.out.println(new String(decode("VXNlcm5hbWU6")));
//    System.out.println(new String(decode("")));
//  }
}
