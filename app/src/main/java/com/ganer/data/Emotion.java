package com.ganer.data;

import android.os.Bundle;

import com.ganer.weather.R;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by shim on 2014. 10. 26..
 */
public class Emotion {
    public static Bundle get(ArrayList<Bundle> weathers) {
        Bundle emotion;

        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH)+1;

        switch(month){
            case 10 :
                    emotion = _10(weathers);
                break;
            case 11 :
                    emotion = _10(weathers);
                break;
            case 12 :
                emotion = _12(weathers);
                break;
            case 1 :
                emotion = _12(weathers);
                break;
            case 2 :
                emotion = _12(weathers);
                break;
            default :
                    emotion = new Bundle();
                    emotion.putInt("emotion", R.drawable.char_1_1);
                    emotion.putString("message", "오늘은 무슨날?...");
                break;
        }

        return emotion;
    }

    public static String _fall(int type){
        String[] msgs;

        switch(type){
            case 2 :
                msgs = new String[]{
                        "꺄악~ 다 젖었어..",
                        "비가온다 추적추적",
                        "가을비를 보고 있으니 센티해 지는데",
                        "이제 날씨가 더 추워 지겠지? 지금도 추운데..",
                        "창밖의 가을비를 보며 마시는 커피 한잔의 여유"
                };
                break;
            case 3 :
                msgs = new String[]{
                        "음.. 왠지 비가 올것 같은데..",
                        "우산 챙겨 가는게 좋을것 같은데?",
                        "에구 무릅 시려.. 비가 오려나?",
                        "구름 많고 바람 많고.. 비 올것 같아"
                };
                break;
            case 4 :
                msgs = new String[]{"꺄악~ 바람이.."};
                break;
            case 5 :
                msgs = new String[]{
                        "오늘처럼 쌀쌀한 날엔 오뎅꼬치~ 너도 먹을래?",
                        "으~~ 넘 추워..",
                        "날 추우니까 옷 좀 두껍게 입어",
                        "추워지니까 낙엽이 더 떨어진다~",
                        "쌀쌀해 졌다.. 추워~"
                };
                break;
            case 6 :
                msgs = new String[]{
                        "날씨 좋다~ 오랫만에 따뜻하네..",
                        "하늘은 높고 나는 살찌는 따뜻한 날씨?",
                        "아직 따뜻하네~ 여행가고 싶은 날"
                };
                break;
            default :
                msgs = new String[]{
                        "선선한게 단풍구경 하기 좋은 날씨네",
                        "가을인데 대하구이 먹고 싶지 않아?",
                        "가을하늘은 역시 맑은 하늘과 밝은 달~",
                        "시원한 바람과 함께 여행 가고 싶다"
                };
                break;
        }

        int a = (int) (Math.random()*msgs.length);
        return msgs[a];
    }

    public static String _winter(int type){
        String[] msgs;

        switch(type){
            case 2 :
                msgs = new String[]{
                        "펄펄~ 눈이 내리네~~"
                };
                break;
            case 3 :
                msgs = new String[]{
                        "음.. 눈이 올것 같은데?",
                        "우산 챙겨 가.. 눈올것 같아"
                };
                break;
            case 4 :
                msgs = new String[]{"꺄악~ 바람이..", "바람 부니까 더 진~~~~짜 추워!!"};
                break;
            case 5 :
                msgs = new String[]{
                        "와~ 엄청 추워!!!",
                        "이런날엔 집밖으로 나가면 안돼",
                        "날 추우니까 옷 좀 두껍게 입어",
                        "여기는 시베리아... 덜덜덜"
                };
                break;
            case 6 :
                msgs = new String[]{
                        "날씨 좋다~ 오랫만에 따뜻하네..",
                        "오? 벌써 봄이오나?",
                        "따뜻해~ 데이트 가자!!"
                };
                break;
            default :
                msgs = new String[]{
                        "날도 추운데 오뎅국물로 몸좀 녹일까?",
                        "겨울이면 이정도는 추워야지",
                        "겨울에는 온천이 딱이에요"
                };
                break;
        }

        int a = (int) (Math.random()*msgs.length);
        return msgs[a];
    }

    public static Bundle _10(ArrayList<Bundle> weathers){
        Bundle emotion = new Bundle();

        int pty1 = weathers.get(0).getInt("pty");  // 현재
        int pty2 = weathers.get(1).getInt("pty");  // 3시간 후
        int pty3 = weathers.get(2).getInt("pty");  // 6시간 후

        if(pty1 > 0){
            //비
            emotion.putInt("emotion", R.drawable.char_3_5);
            emotion.putString("message", _fall(2) ); // 비옴
        }else if(pty2 > 0 || pty3 > 0){
            emotion.putInt("emotion", R.drawable.char_3_1);
            emotion.putString("message", _fall(3));  // 비올것같음.
        }else {
            emotion.putInt("emotion", R.drawable.char_3_1);
            emotion.putString("message", _fall(1));  // 일반
        }

        return emotion;
    }
    public static Bundle _12(ArrayList<Bundle> weathers){
        Bundle emotion = new Bundle();

        int pty1 = weathers.get(0).getInt("pty");  // 현재
        int pty2 = weathers.get(1).getInt("pty");  // 3시간 후
        int pty3 = weathers.get(2).getInt("pty");  // 6시간 후

        if(pty1 > 0){
            emotion.putInt("emotion", R.drawable.char_4_7);
            emotion.putString("message", _winter(2));  // 눈옴
        }else if(pty2 > 0 || pty3 > 0){
            emotion.putInt("emotion", R.drawable.char_4_1);
            emotion.putString("message", "눈이 올것 같은데? 약속 잡을까?");
            emotion.putString("message", _winter(3));  // 올것같음
        }else {
            emotion.putInt("emotion", R.drawable.char_4_1);
            emotion.putString("message", _winter(1));  // 일반
        }

        return emotion;
    }
}
