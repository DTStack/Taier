export const utf16to8 = (str) => {
  let out, i, len, c;
  out = '';
  len = str.length || 0;
  for (i = 0; i < len; i++) {
    c = str.charCodeAt(i);
    if (c >= 0x0001 && c <= 0x007f) {
      out += str.charAt(i);
    } else if (c > 0x07ff) {
      out += String.fromCharCode(0xe0 | ((c >> 12) & 0x0f));
      out += String.fromCharCode(0x80 | ((c >> 6) & 0x3f));
      out += String.fromCharCode(0x80 | ((c >> 0) & 0x3f));
    } else {
      out += String.fromCharCode(0xc0 | ((c >> 6) & 0x1f));
      out += String.fromCharCode(0x80 | ((c >> 0) & 0x3f));
    }
  }
  return out;
};
// utf8转为utf16：
// export const utf8to16 = (str) => {
//   let out, i, len, c;
//   let char2, char3;
//   out = '';
//   len = str.length;
//   i = 0;
//   while (i < len) {
//     c = str.charCodeAt(i++);
//     switch (c >> 4) {
//       case 0:
//       case 1:
//       case 2:
//       case 3:
//       case 4:
//       case 5:
//       case 6:
//       case 7:
//         out += str.charAt(i - 1);
//         break;
//       case 12:
//       case 13:
//         // 110x xxxx 10xx xxxx
//         char2 = str.charCodeAt(i++);
//         out += String.fromCharCode(((c & 0x1f) << 6) | (char2 & 0x3f));
//         break;
//       case 14:
//         // 1110 xxxx 10xx xxxx 10xx xxxx
//         char2 = str.charCodeAt(i++);
//         char3 = str.charCodeAt(i++);
//         out += String.fromCharCode(
//           ((c & 0x0f) << 12) | ((char2 & 0x3f) << 6) | ((char3 & 0x3f) << 0)
//         );
//         break;
//     }
//   }
//   return out;
// };

export const utf8to16 = function (utf8Arr) {
  let utf16Str = '';

  for (let i = 0; i < utf8Arr.length; i++) {
    //每个字节都转换为2进制字符串进行判断
    const one = utf8Arr[i].toString(2);

    //正则表达式判断该字节是否符合>=2个1和1个0的情况
    const v = one.match(/^1+?(?=0)/);

    //多个字节编码
    if (v && one.length == 8) {
      //获取该编码是多少个字节长度
      const bytesLength = v[0].length;

      //首个字节中的数据,因为首字节有效数据长度为8位减去1个0位，再减去bytesLength位的剩余位数
      let store = utf8Arr[i].toString(2).slice(7 - bytesLength);
      for (var st = 1; st < bytesLength; st++) {
        //后面剩余字节中的数据，因为后面字节都是10xxxxxxx，所以slice中的2指的是去除10
        store += utf8Arr[st + i].toString(2).slice(2);
      }

      //转换为Unicode码值
      utf16Str += String.fromCharCode(parseInt(store, 2));

      //调整剩余字节数
      i += bytesLength - 1;
    } else {
      //单个字节编码，和Unicode码值一致，直接将该字节转换为UTF-16
      utf16Str += String.fromCharCode(utf8Arr[i]);
    }
  }

  return utf16Str;
};
