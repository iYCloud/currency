import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class Currency {
    String name;
    float userToInter;
    float interToUser;
    float profit;

    DecimalFormat df = new DecimalFormat("#.#####");

    public String getName(){
        return this.name;
    }

    public float getUserToInter(){
        return this.userToInter;
    }

    public void setUserToInter(float userToInter){
        this.userToInter = userToInter;
    }

    public float getInterToUser(){
        return this.interToUser;
    }

    public void setInterToUser(float interToUser){
        this.interToUser = interToUser;
    }

    public float getProfit(){
        return this.profit;
    }

    public void setProfit(float profit){
        this.profit = profit;
    }

    public List<String> list(){
        return Arrays.asList(this.name, String.valueOf(this.userToInter), String.valueOf(this.interToUser), String.valueOf(df.format(this.profit)));
    }

    public Currency(String name){
        this.name = name;
    }

}
