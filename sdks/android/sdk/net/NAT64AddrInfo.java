package org.jitsi.meet.sdk.net;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NAT64AddrInfo {
   private final String prefix;
   private final String suffix;

   static String bytesToHexString(byte[] bytes) {
      StringBuilder hexStr = new StringBuilder();
      byte[] var2 = bytes;
      int var3 = bytes.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         byte b = var2[var4];
         hexStr.append(String.format("%02X", b));
      }

      return hexStr.toString();
   }

   public static NAT64AddrInfo discover(String host) throws UnknownHostException {
      InetAddress ipv4 = null;
      InetAddress ipv6 = null;
      InetAddress[] var3 = InetAddress.getAllByName(host);
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         InetAddress addr = var3[var5];
         byte[] bytes = addr.getAddress();
         if (bytes.length == 4) {
            ipv4 = addr;
         } else if (bytes.length == 16) {
            ipv6 = addr;
         }
      }

      if (ipv4 != null && ipv6 != null) {
         return figureOutNAT64AddrInfo(ipv4.getAddress(), ipv6.getAddress());
      } else {
         return null;
      }
   }

   static NAT64AddrInfo figureOutNAT64AddrInfo(byte[] ipv4AddrBytes, byte[] ipv6AddrBytes) {
      String ipv6Str = bytesToHexString(ipv6AddrBytes);
      String ipv4Str = bytesToHexString(ipv4AddrBytes);
      int prefixLength = 96;
      int suffixLength = 0;
      String prefix = null;
      String suffix = null;
      if (ipv4Str.equalsIgnoreCase(ipv6Str.substring(prefixLength / 4))) {
         prefix = ipv6Str.substring(0, prefixLength / 4);
      } else {
         ipv6Str = ipv6Str.substring(0, 16) + ipv6Str.substring(18);
         prefixLength = 64;

         for(suffixLength = 6; prefixLength >= 32; suffixLength += 2) {
            if (ipv4Str.equalsIgnoreCase(ipv6Str.substring(prefixLength / 4, prefixLength / 4 + 8))) {
               prefix = ipv6Str.substring(0, prefixLength / 4);
               suffix = ipv6Str.substring(ipv6Str.length() - suffixLength);
               break;
            }

            prefixLength -= 8;
         }
      }

      return prefix != null ? new NAT64AddrInfo(prefix, suffix) : null;
   }

   static String hexStringToIPv6String(String hexStr) {
      return hexStringToIPv6String(new StringBuilder(hexStr));
   }

   static String hexStringToIPv6String(StringBuilder str) {
      for(int i = 28; i > 0; i -= 4) {
         str.insert(i, ":");
      }

      return str.toString().toUpperCase();
   }

   static byte[] ipv4AddressStringToBytes(String ipv4Address) {
      InetAddress address;
      try {
         address = InetAddress.getByName(ipv4Address);
      } catch (UnknownHostException var3) {
         throw new IllegalArgumentException("Invalid IP address: " + ipv4Address, var3);
      }

      byte[] bytes = address.getAddress();
      if (bytes.length != 4) {
         throw new IllegalArgumentException("Not an IPv4 address: " + ipv4Address);
      } else {
         return bytes;
      }
   }

   private NAT64AddrInfo(String prefix, String suffix) {
      this.prefix = prefix;
      this.suffix = suffix;
   }

   public String getIPv6Address(String ipv4Address) {
      byte[] ipv4AddressBytes = ipv4AddressStringToBytes(ipv4Address);
      StringBuilder newIPv6Str = new StringBuilder();
      newIPv6Str.append(this.prefix);
      newIPv6Str.append(bytesToHexString(ipv4AddressBytes));
      if (this.suffix != null) {
         newIPv6Str.insert(16, "00");
         newIPv6Str.append(this.suffix);
      }

      return hexStringToIPv6String(newIPv6Str);
   }
}
