import java.util.*;

class Solution {
    public int solution(int[] money) {
        int N = money.length;
        int[][] stole = new int[2][N];
        int answer = 0;
        for(int i=0; i<2; i++){
            if(i==0){
                stole[0][1] = 0;
            }else{
                stole[0][1] = money[0];
            }
            if(i==0){
                stole[1][1] = money[1];
            }else{
                stole[1][1] = money[0];
            }

            for(int j=2; j<N; j++){
                stole[0][j] = Math.max(stole[0][j-1], stole[1][j-1]);
                stole[1][j] = stole[0][j-1] + money[j];
            }
            if(i==0){
                answer = Math.max(stole[0][N-1], stole[1][N-1]);
            }else{
                answer = Math.max(answer, stole[0][N-1]);
            }

        }

        return answer;
    }
}