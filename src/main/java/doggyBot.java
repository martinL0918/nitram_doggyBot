import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.validation.constraints.Null;
import java.util.*;

import static java.lang.Math.toIntExact;


public class doggyBot extends TelegramLongPollingBot {
    private HashMap<Integer, Dog> dictionary = new HashMap<>();
    public  HashMap<Integer, Integer> growCurve = new HashMap<>();
    public HashMap<String, String> totalAchievements = new HashMap<>();


    public void initalizeGrowCurve() {
        growCurve.put(1,12);
        growCurve.put(2,36);
        growCurve.put(3,80);
        growCurve.put(4,157);
        growCurve.put(5,297);
        growCurve.put(6,549);
        growCurve.put(7,1003);
        growCurve.put(8,1818);
    }
    public void initalizeTotalAchievements(){
        //親密度
        totalAchievements.put("愛狗之人","和狗隻親密度達到30");
        totalAchievements.put("狗的好夥伴","和狗隻親密度達至50");
        totalAchievements.put("你前世係狗","和狗隻親密度達至100");
        //指令系
        totalAchievements.put("新手上路","命令狗隻前往探險");
        totalAchievements.put("殺狗狂徒","曾經企圖殺狗");
        //等級系統
        totalAchievements.put("初嘗昇華","等級達到LV2");
        totalAchievements.put("中庸之人","等級達到LV4");
        totalAchievements.put("究極","等級達到LV8");
        //物品系
        totalAchievements.put("中產","彩產超過$2500");
        totalAchievements.put("大富翁","彩產超過$5000");
        totalAchievements.put("家財萬貫","彩產超過$1000");
        //競技埸
        totalAchievements.put("病態賭徒","在競技埸挑戰10次");

    }
    public void setTimer(int seconds,Update update) {
        dictionary.get(update.getMessage().getFrom().getId()).getTimer().schedule(new TimerTask() {
            @Override
            public void run() {
                dictionary.get(update.getMessage().getFrom().getId()).setExploring(false);
                System.out.println(dictionary.get(update.getMessage().getFrom().getId()) + ":" + dictionary.get(update.getMessage().getFrom().getId()).isExploring());
                String explore_msg ="";
                explore_msg = exploreTheWorld(update);
                SendMessage message = new SendMessage();
                message.setChatId(update.getMessage().getChatId());
                message.setText((dictionary.get(update.getMessage().getFrom().getId()).getName()+ "翻左屋企\uD83C\uDFE0啦\n佢頭先"+explore_msg));
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                }
                    checkHPleft(update);
                    checkExpLevelUp(update);
                try {
                    checkAchievements(update);
                } catch (NullPointerException e) {
                }
            }
        }, seconds * 1000); //?60?-1???
    }
    public void setHungerTimer(Update update) {
        dictionary.get(update.getMessage().getFrom().getId()).getHungerTimer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                dictionary.get(update.getMessage().getFrom().getId()).setHunger(dictionary.get(update.getMessage().getFrom().getId()).getHunger()-1);
                System.out.println(dictionary.get(update.getMessage().getFrom().getId())+": "+dictionary.get(update.getMessage().getFrom().getId()).getHunger());
                checkDead(update);

            }
        }, 15 * 60 * 1000, 15 * 60 * 1000); //每兩分鍾-1
    }
    //睇下隻狗有無名
    public boolean checkHaveName(Update update) {
        if (dictionary.get(update.getMessage().getFrom().getId()).getName().equals("") || dictionary.get(update.getMessage().getFrom().getId()).getName().equals("null"))
            return false;
        else
            return true;
    }

    //設定狗的性別
    public void setGender(Update update) {
        int x = (int) (Math.random() * 2 + 1);
        if (x == 1)
            dictionary.get(update.getMessage().getFrom().getId()).setSex("雄性");
        else
            dictionary.get(update.getMessage().getFrom().getId()).setSex("雌性");

    }

    //check有無空白
    public String getSpace(String aString) {
        String temp = "";
        int counter = aString.indexOf(" ");
        if (counter > 0)
            return aString.substring(counter + 1, aString.length());
        else
            return temp;
    }
    public void checkHPleft(Update update){
        int hp;
        try {
            hp = dictionary.get(update.getMessage().getFrom().getId()).getHealth();
        }catch (NullPointerException exception){
            hp = dictionary.get(update.getCallbackQuery().getFrom().getId()).getHealth();
        }
        String command = update.getMessage().getText();
        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId());
        if (hp <=0){
            message.setText("你隻狗HP歸0\uD83D\uDC94，死左啦\uD83D\uDC80");
            dictionary.get(update.getMessage().getFrom().getId()).setDogAlive(false); //係update果度會DEL DATABASE
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

    }
    public void checkDead(Update update){
        String command = update.getMessage().getText();
        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId());

        if (dictionary.get(update.getMessage().getFrom().getId()).getHunger() <= 0){
            message.setText("你隻狗飢餓值歸0\uD83D\uDC94，死左啦\uD83D\uDC80");
            dictionary.get(update.getMessage().getFrom().getId()).setDogAlive(false); //係update果度會DEL DATABASE
        }
    }
    public int addHP(Update update){
        int currentLevel = dictionary.get(update.getMessage().getFrom().getId()).getLevel();
        if (currentLevel == 2){
            return 45;
        }
        else if (currentLevel == 3){
            return 90;
        }
        else if (currentLevel == 4){
            return 180;
        }
        else if (currentLevel == 5){
            return 360;
        }
        else if (currentLevel == 6){
            return 720;
        }
        else if (currentLevel == 7){
            return 1440;
        }
        else
            return 2880;
    }
    public void checkExpLevelUp(Update update){
        String command = update.getMessage().getText();
        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId());

        if (dictionary.get(update.getMessage().getFrom().getId()).getExp()>= growCurve.get(dictionary.get(update.getMessage().getFrom().getId()).getLevel())){
            dictionary.get(update.getMessage().getFrom().getId()).setLevel(dictionary.get(update.getMessage().getFrom().getId()).getLevel() + 1);
            message.setText("你隻狗Level Up⬆️\n佢而家係LV"+dictionary.get(update.getMessage().getFrom().getId()).getLevel()+
                    "\n佢既最大生命值提升到"+ addHP(update)+"\uD83C\uDF89");
            dictionary.get(update.getMessage().getFrom().getId()).setMaximumHealth(addHP(update));
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

    }
    public int levelUpDMG(Update update,int loss){
        int level = dictionary.get(update.getMessage().getFrom().getId()).getLevel();
        if (level == 1)
            return 1;
        else if (level == 2)
            return (int)Math.round(loss/0.5);
        else if (level == 3)
            return (int)Math.round(loss/0.5/.05);
        else if (level == 4)
            return (int)Math.round(loss/0.5/0.5/0.5);
        else if (level == 5)
            return (int)Math.round(loss/0.5/0.5/0.5/0.5);
        else if (level == 6)
            return (int)Math.round(loss/0.5/0.5/0.5/0.5/0.5);
        else if (level == 7)
            return (int)Math.round(loss/0.5/0.5/0.5/0.5/0.5/0.5);
        else if (level == 8)
            return (int)Math.round(loss/0.5/0.5/0.5/0.5/0.5/0.5/0.5);
        else
             return 1;
    }
    public void checkAchievements(Update update){
        int userID = 0;
        String command ="";
        SendMessage message = new SendMessage();
        try {
            message.setChatId(update.getMessage().getChatId());
            userID = update.getMessage().getFrom().getId();
            command = update.getMessage().getText();
        }catch (NullPointerException e){
            message.setChatId(update.getCallbackQuery().getMessage().getChatId());
            userID = update.getCallbackQuery().getFrom().getId();
        }
        //親密度
        if (dictionary.get(userID).isDogAlive()) {
            if (!dictionary.get(userID).getAchievedAchievements().contains("愛狗之人")) {
                if (dictionary.get(userID).getRelationship() >= 30) {
                    dictionary.get(userID).getAchievedAchievements().add("愛狗之人");
                    message.setText("\uD83C\uDF89\uD83C\uDF89你已解鎖成就\n「愛狗之人」");
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (!dictionary.get(userID).getAchievedAchievements().contains("狗的好夥伴")) {
                if (dictionary.get(userID).getRelationship() >= 50) {
                    dictionary.get(userID).getAchievedAchievements().add("狗的好夥伴");
                    message.setText("\uD83C\uDF89\uD83C\uDF89你已解鎖成就\n「狗的好夥伴」");
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (!dictionary.get(userID).getAchievedAchievements().contains("你前世係狗")) {
                if (dictionary.get(userID).getRelationship() >= 100) {
                    dictionary.get(userID).getAchievedAchievements().add("你前世係狗");
                    message.setText("\uD83C\uDF89\uD83C\uDF89你已解鎖成就\n「你前世係狗」");
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
            //指令系
            if (!dictionary.get(userID).getAchievedAchievements().contains("新手上路")) {
                if (command.equals("/explore")) {
                    dictionary.get(userID).getAchievedAchievements().add("新手上路");
                    message.setText("\uD83C\uDF89\uD83C\uDF89你已解鎖成就\n「新手上路」");
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
            if(!dictionary.get(userID).getAchievedAchievements().contains("殺狗狂徒")) {
                if (command.equals("/kill")) {
                    dictionary.get(userID).getAchievedAchievements().add("殺狗狂徒");
                    message.setText("\uD83C\uDF89\uD83C\uDF89你已解鎖成就\n「殺狗狂徒」");
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
            //等級系
            if (!dictionary.get(userID).getAchievedAchievements().contains("初嘗昇華")) {
                if (dictionary.get(userID).getLevel() >=2) {
                    dictionary.get(userID).getAchievedAchievements().add("初嘗昇華");
                    message.setText("\uD83C\uDF89\uD83C\uDF89你已解鎖成就\n「初嘗昇華」");
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (!dictionary.get(userID).getAchievedAchievements().contains("中庸之人")) {
                if (dictionary.get(userID).getLevel() >=4) {
                    dictionary.get(userID).getAchievedAchievements().add("中庸之人");
                    message.setText("\uD83C\uDF89\uD83C\uDF89你已解鎖成就\n「中庸之人」");
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (!dictionary.get(userID).getAchievedAchievements().contains("究極")) {
                if (dictionary.get(userID).getLevel() >=8) {
                    dictionary.get(userID).getAchievedAchievements().add("究極");
                    message.setText("\uD83C\uDF89\uD83C\uDF89你已解鎖成就\n「究極」");
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
            //物品系
            if (!dictionary.get(userID).getAchievedAchievements().contains("中產")) {
                if (dictionary.get(userID).getCurrency() >= 2500) {
                    dictionary.get(userID).getAchievedAchievements().add("中產");
                    message.setText("\uD83C\uDF89\uD83C\uDF89你已解鎖成就\n「中產」");
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (!dictionary.get(userID).getAchievedAchievements().contains("大富翁")) {
                if (dictionary.get(userID).getCurrency() >= 5000) {
                    dictionary.get(userID).getAchievedAchievements().add("大富翁");
                    message.setText("\uD83C\uDF89\uD83C\uDF89你已解鎖成就\n「大富翁」");
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (!dictionary.get(userID).getAchievedAchievements().contains("家財萬貫")) {
                if (dictionary.get(userID).getCurrency() >= 10000) {
                    dictionary.get(userID).getAchievedAchievements().add("家財萬貫");
                    message.setText("\uD83C\uDF89\uD83C\uDF89你已解鎖成就\n「家財萬貫」");
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
            //競技埸
            if(!dictionary.get(userID).getAchievedAchievements().contains("病態賭徒")) {
                if (dictionary.get(userID).getArenaCounter() == 10) {
                    dictionary.get(userID).getAchievedAchievements().add("病態賭徒");
                    message.setText("\uD83C\uDF89\uD83C\uDF89你已解鎖成就\n「病態賭徒」");
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    public String playGame1(int input, Update update) {
        //0=包 1=剪 2=揼
        String result = "";
        int computer = (int) (Math.random() * (2 - 0 + 1) + 0);
        if (computer == 0) { //電腦出包
            if (input == 2) {//玩家出剪
                result = "你出✌ 我出\uD83D\uDD90\n" + "你贏左\uD83C\uDF8A\uD83C\uDF8A\n" + "但親密度好似無咩變化\n";
            }
            if (input == 1) {//玩家出包
                result = "你出\uD83D\uDD90 我出\uD83D\uDD90\n" + "大家打和\n" + "我對你產生左少少好感\n";
                dictionary.get(update.getCallbackQuery().getFrom().getId()).setRelationship(dictionary.get(update.getCallbackQuery().getFrom().getId()).getRelationship() + 1);
            }
            if (input == 3) {//玩家出揼
                result = "你出\uD83D\uDC4A 我出\uD83D\uDD90" + "\n你輸左\n❌" + "我同你變得更親近";
                dictionary.get(update.getCallbackQuery().getFrom().getId()).setRelationship(dictionary.get(update.getCallbackQuery().getFrom().getId()).getRelationship() + 2);
            }
        } else if (computer == 1) { //電腦出剪
            if (input == 2) {//玩家出剪
                result = "你出✌ 我出✌\n" + "大家打和\n" + "我對你產生左少少好感\n";
                dictionary.get(update.getCallbackQuery().getFrom().getId()).setRelationship(dictionary.get(update.getCallbackQuery().getFrom().getId()).getRelationship() + 1);
            }
            if (input == 1) {//玩家出包
                result = "你出\uD83D\uDD90 我出✌" + "\n你輸左❌\n" + "我同你變得更親近\n";
                dictionary.get(update.getCallbackQuery().getFrom().getId()).setRelationship(dictionary.get(update.getCallbackQuery().getFrom().getId()).getRelationship() + 2);
            }
            if (input == 3) {//玩家出揼
                result = "你出\uD83D\uDC4A 我出✌\n" + "你贏左\uD83C\uDF8A\uD83C\uDF8A\n" + "但親密度好似無咩變化\n";
            }
        } else if (computer == 2) { //電腦出揼
            if (input == 2) {//玩家出剪
                result = "你出✌ 我出\uD83D\uDC4A" + "\n你輸左\n" + "我同你變得更親近\n" ;
                dictionary.get(update.getCallbackQuery().getFrom().getId()).setRelationship(dictionary.get(update.getCallbackQuery().getFrom().getId()).getRelationship() + 2);
            }
            if (input == 1) {//玩家出包
                result = "你出\uD83D\uDD90 我出\uD83D\uDC4A\n" + "你贏左\uD83C\uDF8A\uD83C\uDF8A\n" + "但親密度好似無咩變化\n";
            }
            if (input == 3) {//玩家出揼
                result = "你出\uD83D\uDC4A 我出\uD83D\uDC4A\n" + "大家打和\n" + "我對你產生左少少好感";
                dictionary.get(update.getCallbackQuery().getFrom().getId()).setRelationship(dictionary.get(update.getCallbackQuery().getFrom().getId()).getRelationship() + 1);
            }
        }
        return result;
    }
    public String playGame2(int input, Update update) {
        String result = "";
        int relation = (int) (Math.random() * ( 2-1 + 1)+ 1);
        int computer = (int) (Math.random() * (2 - 0 + 1) + 0);
        System.out.println(computer);
        if (computer == 0) {//粒鑽石係左邊i
            result = "\uD83D\uDC8E\t\uD83E\uDDF3\t\uD83E\uDDF3\n";
            if(input==1){
               result += "你答岩左，粒鑽石真係係左邊\n";
               result += "你同"+dictionary.get(update.getCallbackQuery().getFrom().getId()).getName() +"既親密度提高左" + relation ;
                dictionary.get(update.getCallbackQuery().getFrom().getId()).setRelationship(dictionary.get(update.getCallbackQuery().getFrom().getId()).getRelationship() + relation);
            }
            else{
                result += "你答錯左，粒鑽石係左邊";
            }
        }
        else if (computer == 1) {//粒鑽石係中間
            result = "\uD83E\uDDF3\t\uD83D\uDC8E\t\uD83E\uDDF3\n";
            if(input==2){
                result += "你答岩左，粒鑽石真係係中間\n";
                result += "你同"+dictionary.get(update.getCallbackQuery().getFrom().getId()).getName() +"既親密度提高左" + relation ;
                dictionary.get(update.getCallbackQuery().getFrom().getId()).setRelationship(dictionary.get(update.getCallbackQuery().getFrom().getId()).getRelationship() + relation);
            }
            else{
                result += "你答錯左，粒鑽石係中間";
            }
        }
        else if (computer == 2) {//粒鑽石係右邊
            result = "\uD83E\uDDF3\t\uD83E\uDDF3\t\uD83D\uDC8E\n";
            if(input==3){
                result += "你答岩左，粒鑽石真係係右邊\n";
                result += "你同"+dictionary.get(update.getCallbackQuery().getFrom().getId()).getName() +"既親密度提高左" + relation ;
                dictionary.get(update.getCallbackQuery().getFrom().getId()).setRelationship(dictionary.get(update.getCallbackQuery().getFrom().getId()).getRelationship() + relation);
            }
            else{
                result += "你答錯左，粒鑽石係右邊";
            }
        }
        return result;
    }
    public String playCombat(int input,Update update) {
        String result = "";
        //int relation = (int) (Math.random() * ( 2-1 + 1)+ 1);
        int computer = (int) (Math.random() * (2 - 0 + 1) + 0);//對手係咩屬性
        int win = (int) (Math.random() * (1 - 0 + 1) + 0); //如果同屬性，邊個會贏
        int winMoney = (int) (Math.random() * (500 - 150 + 1) + 150);
        int loseMoney = (int) (Math.random() * (650 - 150 + 1) + 150);
        int arenaHealth = dictionary.get(update.getCallbackQuery().getFrom().getId()).getHealth();
        int arenaMaxHealth = dictionary.get(update.getCallbackQuery().getFrom().getId()).getMaximumHealth();
        String petName = dictionary.get(update.getCallbackQuery().getFrom().getId()).getName();

        if (computer == 0) {//電腦係快攻手
            if (input == 0) {//快攻手
                result += "\uD83D\uDC36" +petName + "使用(快攻)風格戰鬥⚔️\n\n";
                if (win == 0) {
                    result +=  petName + "遇到(快攻手)佛山無影腳- 杰克\n\t\t🗡️" +
                             petName + "開始同杰克展開生死對決\n\t\t🗡️" +
                             petName + "一野咬落杰克到" +
                            "\t\t🗡️️杰克都不甘視弱,使出佛山無影腳\n\t\t🗡️" +
                             petName + "中左7749腳\n\t\t🗡️" +
                             petName + "\t\t不幸戰敗\n\n";
                    result +=  petName + "損失左$" + loseMoney + "💸\n";
                    dictionary.get(update.getCallbackQuery().getFrom().getId()).setCurrency(dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() - loseMoney);
                    if (arenaHealth > 10) {
                        result +=  petName + "只係得翻50%血量💔\n";
                        dictionary.get(update.getCallbackQuery().getFrom().getId()).setHealth((int) Math.round(dictionary.get(update.getCallbackQuery().getFrom().getId()).getHealth() * 0.5));
                    } else {
                        result += "\n因為你賭命\n" + petName + "已經死左💀";
                        dictionary.get(update.getCallbackQuery().getFrom().getId()).setDogAlive(false);
                    }
                }
                if (win == 1) {
                    result +=  petName + "遇到(快攻手)佛山無影腳- 杰克\n" +
                             petName + "開始同杰克展開生死對決\n\t\t🗡️" +
                             petName + "一野咬落杰克到\n" +
                            "\t\t🗡️️杰克都不甘視弱,使出佛山無影腳\n\t\t🗡️" +
                             petName + "使出電光石火，衝去杰克\n" +
                            "\t\t🗡️️佢一野咬住杰克既腳令到佢出唔到招\n\t\t🗡️" +
                            petName + "乘勝狙擊,成功打敗左仔杰克\n\n";
                    result +=  petName + "獲得$" + winMoney + "✔️勝利獎勵";
                    dictionary.get(update.getCallbackQuery().getFrom().getId()).setCurrency(dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() + winMoney);
                }

            } else if (input == 1) {//狗係重攻擊
                result += "\uD83D\uDC36" +petName + "使用(重攻擊)風格戰鬥⚔️\n\n";
                result += petName + "遇到(快攻手)佛山無影腳- 杰克\n" +
                         petName + "開始同杰克展開生死對決\n\t\t🗡️" +
                          petName + "原地不動，係度蓄力儲氣,希望可以一野打低杰克\n" +
                        "\t\t🗡️️杰克看見機不可失，即刻衝上前\n" +
                        "\t\t🗡️️杰克以" +  petName + "為中心，不斷使出回旋斬\n\t\t🗡️" +
                         petName + "的攻擊不單止打唔中杰克，仲比佢斬左幾百刀\n\t\t🗡️" +
                         petName + "戰敗\n\n";
                result +=  petName + "損失左$" + loseMoney + "💸\n";
                dictionary.get(update.getCallbackQuery().getFrom().getId()).setCurrency(dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() - loseMoney);
                if (arenaHealth > 10) {
                    result +=  petName + "只係得翻50%血量💔\n";
                    dictionary.get(update.getCallbackQuery().getFrom().getId()).setHealth((int) Math.round(dictionary.get(update.getCallbackQuery().getFrom().getId()).getHealth() * 0.5));
                } else {
                    result += "\n因為你賭命\n" +  petName + "已經死左💀";
                    dictionary.get(update.getCallbackQuery().getFrom().getId()).setDogAlive(false);
                }
            } else {//狗係防禦型
                result += "\uD83D\uDC36" +petName + "使用(防禦)風格戰鬥⚔️\n\n";
                result +=  petName + "遇到(快攻手)佛山無影腳- 杰克\n" +
                         petName + "開始同杰克展開生死對決\n\t\t🗡️" +
                         petName + "原地不動擺出防禦架勢\n" +
                        "\t\t🗡️️杰克拎起武器即刻衝上前,不斷對" +  petName + "使出快刺\n\t\t🗡️" +
                          petName + "中左幾百劍\n" +
                        "\t\t🗡️️但" +  petName + "竟然絲毫無損\n" +
                        "\t\t🗡️️係呢個時候，杰克已經體力不支\n\t\t🗡️" +
                         petName + "把握機會使出重擊，成功打敗左杰克\n\n";
                result +=  petName + "獲得$" + winMoney + "✔️勝利獎勵";
                dictionary.get(update.getCallbackQuery().getFrom().getId()).setCurrency(dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() + winMoney);
            }
        }
        if (computer == 1) {//電腦係重戰士
            if (input == 1) {//狗係重攻擊
                result += "\uD83D\uDC36" +petName + "使用(重攻擊)風格戰鬥⚔️\n\n";
                if (win == 0) {
                    result +=  petName + "遇到(重戰士)生死一擊- 海道\n" +
                             petName + "開始同海道展開生死對決\n\t\t🗡️" +
                             petName + "使出吃奶之力，一野撞埋海道度\n" +
                            "\t\t🗡️️海道都不甘視弱,拿出巨大斧頭，向下一劈\n\t\t🗡️" +
                             petName + "背部受重創\n\t\t🗡️" +
                            "不幸戰敗\n\n";
                    result +=  petName + "損失左$" + loseMoney + "💸\n";
                    dictionary.get(update.getCallbackQuery().getFrom().getId()).setCurrency(dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() - loseMoney);
                    if (arenaHealth > 10) {
                        result +=  petName + "只係得翻50%血量💔\n";
                        dictionary.get(update.getCallbackQuery().getFrom().getId()).setHealth((int) Math.round(arenaMaxHealth * 0.5));
                    } else {
                        result += "\n因為你賭命\n" +  petName + "已經死左💀";
                        dictionary.get(update.getCallbackQuery().getFrom().getId()).setDogAlive(false);
                    }
                }
                if (win == 1) {
                    result += petName + "遇到(重戰士)生死一擊- 海道\n\t\t🗡️" +
                             petName + "開始同海道展開生死對決\n\t\t🗡️" +
                             petName + "氣定神閒，專注儲氣\n\t\t🗡️" +
                            "海道衝上前，打算拎斧頭砍落" + petName + "度\n\t\t🗡️" +
                             petName + "係電石火石一刻咬住杰克隻腳\n" +
                            "\t\t🗡️️海道失血過多，昏迷不醒\n\t\t🗡️" +
                              petName + "打敗左海道\n\n";
                    result +=  petName + "獲得$" + winMoney + "✔️勝利獎勵";
                    dictionary.get(update.getCallbackQuery().getFrom().getId()).setCurrency(dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() + winMoney);
                }

            } else if (input == 2) {//狗係防禦
                result += "\uD83D\uDC36" +petName + "使用(防禦)風格戰鬥⚔️\n\n";
                result +=  petName + "遇到(重戰士)生死一擊- 海道\n" +
                         petName + "開始同海道展開生死對決\n\t\t🗡️" +
                         petName + "原地不動，使出銅牆鐵壁\n" +
                        "\t\t🗡️️海道克一邊蓄力，一邊走向" +  petName + "\n" +
                        "\t\t🗡️️海道使勁拿斧頭一砍\n\t\t🗡️" +
                          petName + "受唔住，當埸昏迷\n\t\t🗡️" +
                         petName + "戰敗\n\n";
                result += petName + "損失左$" + loseMoney + "💸\n";
                dictionary.get(update.getCallbackQuery().getFrom().getId()).setCurrency(dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() - loseMoney);
                if (arenaHealth > 10) {
                    result +=  petName + "只係得翻50%血量💔\n";
                    dictionary.get(update.getCallbackQuery().getFrom().getId()).setHealth((int) Math.round(dictionary.get(update.getCallbackQuery().getFrom().getId()).getHealth() * 0.5));
                } else {
                    result += "\n因為你賭命\n" +  petName + "已經死左💀";
                    dictionary.get(update.getCallbackQuery().getFrom().getId()).setDogAlive(false);
                }
            } else {//狗係輕攻擊
                result += "\uD83D\uDC36" +petName + "使用(快攻)風格戰鬥⚔️\n\n";
                result +=  petName + "遇到(重戰士)生死一擊- 海道\n\t\t🗡️" +
                          petName + "開始同海道展開生死對決\n\t\t🗡️" +
                         petName + "使出電光一閃，衝向海道\n" +
                        "\t\t🗡️️海道儲力向前一砍，打算了結" + petName + "\n\t\t🗡️" +
                         petName + "避開左海道的攻擊\n\t\t🗡️" +
                          petName + "不斷向海道使出快攻\n" +
                        "\t\t🗡️️海道招架不住，戰敗\n\n";
                result +=  petName + "獲得$" + winMoney + "✔️勝利獎勵";
                dictionary.get(update.getCallbackQuery().getFrom().getId()).setCurrency(dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() + winMoney);
            }
        }
        if (computer == 2) {//電腦係坦克
            if (input == 2) {//狗係防禦
                result += "\uD83D\uDC36" +petName + "使用(防禦)風格戰鬥⚔️\n\n";
                if (win == 0) {
                    result +=  petName + "遇到(坦克)鋼鐵堡壘- 大媽\n" +
                              petName + "開始同大媽展開生死對決\n\t\t🗡️" +
                              petName + "使出銅牆鐵壁\n" +
                            "\t\t🗡️️大媽都不甘視弱,使出鋼鐵肌膚\n" +
                            "\t\t🗡️️雙方不斷使出撞擊，令到地動山搖\n" +
                             "\t\t🗡️" +  petName + "不敵大媽的鋼鐵肌膚\n" +
                            "\t\t🗡️️不幸戰敗\n\n";
                    result +=  petName + "損失左$" + loseMoney + "💸\n";
                    dictionary.get(update.getCallbackQuery().getFrom().getId()).setCurrency(dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() - loseMoney);
                    if (arenaHealth > 10) {
                        result +=  petName + "只係得翻50%血量💔\n";
                        dictionary.get(update.getCallbackQuery().getFrom().getId()).setHealth((int) Math.round(dictionary.get(update.getCallbackQuery().getFrom().getId()).getHealth() * 0.5));
                    } else {
                        result += "\n因為你賭命\n" +  petName + "已經死左💀";
                        dictionary.get(update.getCallbackQuery().getFrom().getId()).setDogAlive(false);
                    }
                }
                if (win == 1) {
                    result +=  petName + "遇到(坦克)鋼鐵堡壘- 大媽\n" +
                             petName + "開始同大媽展開生死對決\n\t\t🗡️" +
                              petName + "使出銅牆鐵壁\n" +
                            "\t\t🗡️️大媽都不甘視弱,使出鋼鐵肌膚\n" +
                            "\t\t🗡️️雙方不斷使出撞擊，令到地動山搖\n" +
                             petName + "係即將戰敗之際，使出仰天長嘯\n\t\t🗡️" +
                             petName + "憑毅力戰勝大媽\n️" ;
                    result +=  petName + "獲得$" + winMoney + "✔️勝利獎勵";
                    dictionary.get(update.getCallbackQuery().getFrom().getId()).setCurrency(dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() + winMoney);
                }

            } else if (input == 1) {//狗係輕攻擊
                result += "\uD83D\uDC36" +petName + "使用(快攻)風格戰鬥⚔️\n\n";
                result +=  petName + "遇到(坦克)鋼鐵堡壘- 大媽\n" +
                          petName + "開始同大媽展開生死對決\n" +
                        "\t\t🗡️️大媽使出鋼鐵肌膚同屹立不倒\n\t\t🗡️" +
                         petName + "不斷向大媽使出攻擊\n" +
                        "\t\t🗡️️但好似對大媽一啲用都無\n" +
                        "\t\t🗡️" + petName + "開始體力不支\n" +
                        "\t\t🗡️️大媽乘勝追擊，對" +  petName + "使出泰山壓頂\n\t\t🗡️" +
                         petName + "戰敗\n\n";
                result +=  petName + "損失左$" + loseMoney + "💸\n";
                dictionary.get(update.getCallbackQuery().getFrom().getId()).setCurrency(dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() - loseMoney);
                if (arenaHealth > 10) {
                    result +=  petName + "只係得翻50%血量💔\n";
                    dictionary.get(update.getCallbackQuery().getFrom().getId()).setHealth((int) Math.round(dictionary.get(update.getCallbackQuery().getFrom().getId()).getHealth() * 0.5));
                } else {
                    result += "\n因為你賭命\n" +  petName + "已經死左💀";
                    dictionary.get(update.getCallbackQuery().getFrom().getId()).setDogAlive(false);
                }
            } else {//狗係重攻擊
                result += "\uD83D\uDC36" +petName + "使用(重攻擊)風格戰鬥⚔️\n\n";
                result +=  petName + "遇到(坦克)鋼鐵堡壘- 大媽\n\t\t🗡️" +
                          petName + "開始同大媽展開生死對決\n\t\t🗡️" +
                          petName + "蓄力衝向大媽\n" +
                        "\t\t🗡️️大媽使出鋼鐵肌膚同屹立不倒\n\t\t🗡️" +
                          petName + "使出憤力一咬\n" +
                        "\t\t🗡️️大媽抵當不住，戰敗\n\n";
                result +=  petName + "獲得$" + winMoney + "✔️勝利獎勵";
                dictionary.get(update.getCallbackQuery().getFrom().getId()).setCurrency(dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() + winMoney);
            }
        }
        if (dictionary.get(update.getCallbackQuery().getFrom().getId()).getHealth() <10){
            result += "\n\n‼注意‼\n‼‼‼‼‼你血量係10以下‼‼‼‼‼\n再輸\uD83D\uDC36"+petName+"就會死\uD83D\uDC80";
        }
        dictionary.get(update.getCallbackQuery().getFrom().getId()).setArenaCounter(dictionary.get(update.getCallbackQuery().getFrom().getId()).getArenaCounter() + 1);
        return result;
    }
    public String exploreTheWorld(Update update){
        String result ="";

        int randomStatement = (int) (Math.random() * (12-0+1)+0);
        if (randomStatement == 0){
            int cash = (int) (Math.random() * (50-1+1)+1);
            int exp = (int) (Math.random() * (12 -1 + 1)+ 1)*dictionary.get(update.getMessage().getFrom().getId()).getLevel();
            result += "搵到一個寶箱\uD83C\uDF81\n";
            result += "打開後有$" +cash;
            result += "同埋陳年古書\n" +
                      "閱讀後好似學識淵博左\n" +
                     "得到" + exp +"\n";
            dictionary.get(update.getMessage().getFrom().getId()).setCurrency(dictionary.get(update.getMessage().getFrom().getId()).getCurrency()+cash);
            dictionary.get(update.getMessage().getFrom().getId()).setExp(dictionary.get(update.getMessage().getFrom().getId()).getExp() + exp);
        }
        if (randomStatement == 1){
            int cash = (int) (Math.random() * (80-30+1)+30);
            int loss = (int) (Math.random() * (10-1+1)+1);
            loss = levelUpDMG(update,loss);
            int exp = (int) (Math.random() * (12 -1 + 1)+ 1) * dictionary.get(update.getMessage().getFrom().getId()).getLevel();
            result += "遇到BOSS-穿著博士袍的黃色章魚老師\uD83D\uDC7E\n";
                result += "\n佢打輸左比黃色章魚老師\uD83D\uDC7E\n生命\uD83D\uDC94減左"+loss;
                dictionary.get(update.getMessage().getFrom().getId()).setHealth(dictionary.get(update.getMessage().getFrom().getId()).getHealth()-loss);
            }
        if (randomStatement == 2){
            int cash = (int) (Math.random() * (50-1+1)+1);
            int exp = (int) (Math.random() * (12 -1 + 1)+ 1)*dictionary.get(update.getMessage().getFrom().getId()).getLevel();
            result += "係地下執到錢\uD83D\uDCB0\n";
            result+= "有$"+ cash;
            result+="\n同埋" +exp + "exp" ;
            dictionary.get(update.getMessage().getFrom().getId()).setCurrency(dictionary.get(update.getMessage().getFrom().getId()).getCurrency()+cash);
            dictionary.get(update.getMessage().getFrom().getId()).setExp(dictionary.get(update.getMessage().getFrom().getId()).getExp() + exp);
        }
        if (randomStatement == 3){
            int cash = (int) (Math.random() * (80-30+1)+30);
            int win = (int) (Math.random() * (1-0+1)+0);
            int loss = (int) (Math.random() * (10-1+1)+1);
            loss = levelUpDMG(update,loss);
            int exp = (int) (Math.random() * (12 -1 + 1)+ 1) * dictionary.get(update.getMessage().getFrom().getId()).getLevel();
            result += "遇到HTML怪物\uD83D\uDC7E\n";
            if (win==0){
                result+="\n佢成功打敗HTML怪物\n佢賺左$"+cash+ "\n同埋"+exp+"exp";
                dictionary.get(update.getMessage().getFrom().getId()).setCurrency(dictionary.get(update.getMessage().getFrom().getId()).getCurrency()+cash);
                dictionary.get(update.getMessage().getFrom().getId()).setExp(dictionary.get(update.getMessage().getFrom().getId()).getExp() + exp);

            }
            if (win == 1) {
                result += "\n佢打輸左比HTML怪物\uD83D\uDC7E\n生命\uD83D\uDC94減左"+loss;
                dictionary.get(update.getMessage().getFrom().getId()).setHealth(dictionary.get(update.getMessage().getFrom().getId()).getHealth()-loss);
            }
        }
        if (randomStatement == 4){
            int cash = (int) (Math.random() * (300-100+1)+100);
            int exp = (int) (Math.random() * (20 -1 + 1)+ 1) *dictionary.get(update.getMessage().getFrom().getId()).getLevel() ;
            result += "發現左Insight的徽章\uD83E\uDD47\n";
            result +="賣左之後有$"+cash;
            result+="\n同埋" +exp + "exp" ;
            dictionary.get(update.getMessage().getFrom().getId()).setCurrency(dictionary.get(update.getMessage().getFrom().getId()).getCurrency()+cash);
            dictionary.get(update.getMessage().getFrom().getId()).setExp(dictionary.get(update.getMessage().getFrom().getId()).getExp() + exp);
        }
        if (randomStatement == 5){
            result += "識到一個新朋友\uD83C\uDF8E\n";
            result +="心靈❤️富足比金錢\uD83D\uDCB5重要";
        }
        if (randomStatement == 6){
            result += "出門口撞到腳\n狀態唔好\uD83D\uDE2D探險失敗❌";//有一個cross
        }
        if (randomStatement == 7){
            int cash = (int) (Math.random() * (80-30+1)+30);
            int win = (int) (Math.random() * (1-0+1)+0);
            int loss = (int) (Math.random() * (10-1+1)+1);
            loss = levelUpDMG(update,loss);
            int exp = (int) (Math.random() * (12 -1 + 1)+ 1)*dictionary.get(update.getMessage().getFrom().getId()).getLevel();
            result += "遇到CSS怪物\uD83D\uDC7E\n";
            if (win==0){
                result+="\n佢成功打敗CSS怪物\n佢賺左$"+cash+ "\n同埋"+exp+"exp";
                dictionary.get(update.getMessage().getFrom().getId()).setCurrency(dictionary.get(update.getMessage().getFrom().getId()).getCurrency()+cash);
                dictionary.get(update.getMessage().getFrom().getId()).setExp(dictionary.get(update.getMessage().getFrom().getId()).getExp() + exp);

            }
            if (win == 1) {
                result += "\n佢打輸左比CSS怪物\uD83D\uDC7E\n生命\uD83D\uDC94減左"+loss;
                dictionary.get(update.getMessage().getFrom().getId()).setHealth(dictionary.get(update.getMessage().getFrom().getId()).getHealth()-loss);

            }
        }
        if (randomStatement == 8){
            int cash = (int) (Math.random() * (80-30+1)+30);
            int win = (int) (Math.random() * (1-0+1)+0);
            int loss = (int) (Math.random() * (10-1+1)+1);
            loss = levelUpDMG(update,loss);
            int exp = (int) (Math.random() * (12 -1 + 1)+ 1)*dictionary.get(update.getMessage().getFrom().getId()).getLevel();
            result += "遇到Javascript怪物\uD83D\uDC7E\n";
            if (win==0){
                result+="\n佢成功打敗Javascript怪物\n佢賺左$"+cash+ "\n同埋"+exp+"exp";
                dictionary.get(update.getMessage().getFrom().getId()).setCurrency(dictionary.get(update.getMessage().getFrom().getId()).getCurrency()+cash);
                dictionary.get(update.getMessage().getFrom().getId()).setExp(dictionary.get(update.getMessage().getFrom().getId()).getExp() + exp);

            }
            if (win == 1) {
                result += "\n佢打輸左比Javascript怪物\uD83D\uDC7E\n生命\uD83D\uDC94減左"+loss;
                dictionary.get(update.getMessage().getFrom().getId()).setHealth(dictionary.get(update.getMessage().getFrom().getId()).getHealth()-loss);
            }
        }
        if (randomStatement == 9){
            int cash = (int) (Math.random() * (80-30+1)+30);
            int win = (int) (Math.random() * (1-0+1)+0);
            int loss = (int) (Math.random() * (10-1+1)+1);
            loss = levelUpDMG(update,loss);
            int exp = (int) (Math.random() * (12 -1 + 1)+ 1)*dictionary.get(update.getMessage().getFrom().getId()).getLevel();
            result += "遇到React怪物\uD83D\uDC7E\n";
            if (win==0){
                result+="\n佢成功打敗React怪物\n佢賺左$"+cash+ "\n同埋"+exp+"exp";
                dictionary.get(update.getMessage().getFrom().getId()).setCurrency(dictionary.get(update.getMessage().getFrom().getId()).getCurrency()+cash);
                dictionary.get(update.getMessage().getFrom().getId()).setExp(dictionary.get(update.getMessage().getFrom().getId()).getExp() + exp);

            }
            if (win == 1) {
                result += "\n佢打輸左比React怪物\uD83D\uDC7E\n生命\uD83D\uDC94減左"+loss;
                dictionary.get(update.getMessage().getFrom().getId()).setHealth(dictionary.get(update.getMessage().getFrom().getId()).getHealth()-loss);
            }
        }
        if (randomStatement == 10){
           result += "搵到治療之泉,恢復左10HP";
            if ((dictionary.get(update.getMessage().getFrom().getId()).getHealth() + 10) > dictionary.get(update.getMessage().getFrom().getId()).getMaximumHealth()) {
                dictionary.get(update.getMessage().getFrom().getId()).setHealth(dictionary.get(update.getMessage().getFrom().getId()).getMaximumHealth()); //set最大生命值
            }
            else{
                dictionary.get(update.getMessage().getFrom().getId()).setHealth(dictionary.get(update.getMessage().getFrom().getId()).getHealth() + 10);
            }
        }
        if (randomStatement ==11){
            int cash = (int) (Math.random() * (50-1+1)+1);
            result += "遇到一個陌生人塞錢比自己\uD83C\uDF81\n";
            result += "有$" +cash;
            dictionary.get(update.getMessage().getFrom().getId()).setCurrency(dictionary.get(update.getMessage().getFrom().getId()).getCurrency()+cash);
        }
        if (randomStatement == 12){
            int cash = (int) (Math.random() * (50-1+1)+1);
            result += "打劫成功\uD83C\uDF81\n";
            result += "賺左$" +cash;
            dictionary.get(update.getMessage().getFrom().getId()).setCurrency(dictionary.get(update.getMessage().getFrom().getId()).getCurrency()+cash);
        }
        return result;
    }

    public void onUpdateReceived(Update update) {
        int userID = 0;
        if (update.hasMessage() && update.getMessage().hasText()) {
            userID = update.getMessage().getFrom().getId();
            if (!dictionary.containsKey(update.getMessage().getFrom().getId())) { //如果無
                dictionary.put(update.getMessage().getFrom().getId(), new Dog());
                update.getMessage().getFrom().getId();

            } else if (dictionary.containsKey((update.getMessage().getFrom().getId()))) {//如果有
                update.getMessage().getFrom().getId();
            }

            if (dictionary.get(update.getMessage().getFrom().getId()).isDogAlive()) {
                if (!dictionary.get(update.getMessage().getFrom().getId()).isExploring()) {

                    System.out.println("Received message from " + update.getMessage().getFrom().getFirstName() + ": " + update.getMessage().getText());
                    //睇下dictionary原本有無呢個人
                    String command = update.getMessage().getText();
                    SendMessage message = new SendMessage();
                    message.setChatId(update.getMessage().getChatId());

                    //當玩家輸入/dog
                    if (command.equals("/dog")) {
                        if (dictionary.get(update.getMessage().getFrom().getId()).getDogCounter() < 1) {
                            initalizeGrowCurve();
                            initalizeTotalAchievements();
                            setGender(update);
                            message.setText("野生的" + dictionary.get(update.getMessage().getFrom().getId()).getSex() + "狗\uD83D\uDC15出現了\n輸入/name [名字]  去幫隻狗改名:\n ( e.g./name 阿旺)");
                            dictionary.get(update.getMessage().getFrom().getId()).setDogCounter(1);
                            setHungerTimer(update);
                        } else {
                            message.setText("你已經有" + dictionary.get(update.getMessage().getFrom().getId()).getName() + "我了\uD83D\uDC36 (汪!!!!))");
                        }
                        try {
                            execute(message);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    if (command.equals("/start")) {
                        message.setText("輸入 /dog去搵一隻狗");
                        try {
                            execute(message);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }

                    //當玩家輸入/name
                    if (command.contains("/name")) {
                        String temp = getSpace(update.getMessage().getText());
                        if (dictionary.get(update.getMessage().getFrom().getId()).getDogCounter() > 0) {
                            if (!checkHaveName(update)) {
                                if (!temp.equals("")) {
                                    dictionary.get(update.getMessage().getFrom().getId()).setName(temp);
                                    message.setText("\uD83D\uDC36我叫做" + dictionary.get(update.getMessage().getFrom().getId()).getName());
                                } else
                                    message.setText("錯啦,識唔識打指令");
                            } else
                                message.setText("\uD83D\uDC36我叫做" + dictionary.get(update.getMessage().getFrom().getId()).getName());
                        } else {
                            message.setText("輸入 /dog 去搵隻狗先");
                        }

                        try {
                            execute(message);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    //當玩家輸入/add
                    if (command.equals("/add")) {
                        dictionary.get(update.getMessage().getFrom().getId()).setRelationship(dictionary.get(update.getMessage().getFrom().getId()).getRelationship() + 1);
                    }
                    //當玩家輸入/info
                    if (command.equals("/info")) {
                        if (dictionary.get(update.getMessage().getFrom().getId()).getDogCounter() > 0) {
                            if (checkHaveName(update)) {
                                message.setText("\uD83D\uDC15狗的資訊\n" +
                                        "\uD83D\uDC36名字: " + dictionary.get(update.getMessage().getFrom().getId()).getName() + "\n" +
                                        "♂️♀️性別: " + dictionary.get(update.getMessage().getFrom().getId()).getSex() + "\n" +
                                        "\uD83D\uDCAA生命: " + dictionary.get(update.getMessage().getFrom().getId()).getHealth() + "/" + dictionary.get(update.getMessage().getFrom().getId()).getMaximumHealth() + "\n" +
                                        "\uD83E\uDD47等級: " + dictionary.get(update.getMessage().getFrom().getId()).getLevel() + "\n" +
                                        "\uD83C\uDD99經驗值: " + dictionary.get(update.getMessage().getFrom().getId()).getExp() + "/" + growCurve.get(dictionary.get(update.getMessage().getFrom().getId()).getLevel()) + "exp\n" +
                                        "\uD83C\uDF54飢餓值: " + dictionary.get(update.getMessage().getFrom().getId()).getHunger() + "/100\n" +
                                        "❤親密度: " + dictionary.get(update.getMessage().getFrom().getId()).getRelationship() + "\n" +
                                        "\uD83D\uDCB0金錢: " + dictionary.get(update.getMessage().getFrom().getId()).getCurrency());
                            } else {
                                message.setText("輸入/name [name] 幫我改過名先啦");
                            }


                        } else {
                            message.setText("輸入 /dog 去搵隻狗先");
                        }

                        try {
                            execute(message);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    if (command.equals(("/achievements"))) {
                        if (dictionary.get(update.getMessage().getFrom().getId()).getDogCounter() > 0) {
                            if (checkHaveName(update)) {
                                String welcoming = "成就系統\n" +
                                        "你已達成" + dictionary.get(update.getMessage().getFrom().getId()).getAchievedAchievements().size() + "/" + totalAchievements.size() + "個成就\n\n";
                                String printAll = "";
                                for (String temp : totalAchievements.keySet()) {
                                    for (String archievedTemp : dictionary.get(update.getMessage().getFrom().getId()).getAchievedAchievements()) {
                                        if (archievedTemp.equals(temp)) {
                                            printAll += "\uD83C\uDF1F「" + temp + "」" + "\n\t\t\t\t-" + totalAchievements.get(temp) + "\n";
                                        }
                                    }
                                }
                                message.setText(welcoming + printAll);
                            } else {
                                message.setText("輸入/name [name] 幫我改過名先啦");
                            }
                        } else {
                            message.setText("輸入 /dog 去搵隻狗先");
                        }
                        try {
                            execute(message);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    if (command.equals("/inventory")) {
                        String result="";
                        if (dictionary.get(update.getMessage().getFrom().getId()).getDogCounter() > 0) {
                            if (checkHaveName(update)) {
                                result += "你既物品庫裹面有\n";
                                for (String temp:dictionary.get(update.getMessage().getFrom().getId()).getInventory().keySet()){
                                    result += temp + "數量:" + dictionary.get(update.getMessage().getFrom().getId()).getInventory().get(temp) + "\n";
                                }
                                message.setText(result);
                            } else {
                                message.setText("輸入/name [name] 幫我改過名先啦");
                            }


                        } else {
                            message.setText("輸入 /dog 去搵隻狗先");
                        }

                        try {
                            execute(message);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    if (command.equals("/version")) {
                        message.setText("小狗的RPG歷險記\uD83D\uDC36\n" +
                                "V1.4更新(10/11/2019)\n" +
                                "-新增物品庫系統\n" +
                                "-新增回復藥\n" +
                                "-新增店鋪\n" +
                                "-修復競技場系統文本錯誤\n" +
                                "\n" +
                                "V1.3更新(9/11/2019)\n" +
                                "-競技場系統正式上線\n" +
                                "-增加新成就\n" +
                                "-修復/play 小遊戲2 錯誤\n" +
                                "\n" +
                                "\uD83D\uDC36\uD83D\uDC36\uD83D\uDC36\uD83D\uDC36\uD83D\uDC36\uD83D\uDC36\uD83D\uDC36\uD83D\uDC36\uD83D\uDC36\uD83D\uDC36\uD83D\uDC36\uD83D\uDC36\uD83D\uDC36\uD83D\uDC36\n" +
                                "更新預告\uD83D\uDD5B\n" +
                                "\uD83C\uDF1FV1.5更新\n" +
                                "-新增好友系統\n" +
                                "-新增物品庫\n" +
                                "-新增送禮系統\n" +
                                "\n" +
                                "\uD83D\uDD19\uD83D\uDD19\uD83D\uDD19\uD83D\uDD19\uD83D\uDD19\uD83D\uDD19\uD83D\uDD19\uD83D\uDD19\uD83D\uDD19\uD83D\uDD19\uD83D\uDD19\uD83D\uDD19\uD83D\uDD19\uD83D\uDD19\n" +
                                "舊版本資訊\n" +
                                "\uD83C\uDF1FV1.2.2更新(8/11/2019)\n" +
                                "-增加 /play 新遊戲\n" +
                                "-完善等級系統\n" +
                                "-進一步測試成就系統\n" +
                                "\n" +
                                "\uD83C\uDF1FV1.2.1更新(7/11/2019)\n" +
                                "-修復大量文本錯誤\n" +
                                "-更改遊戲平衡性\n" +
                                "-測試成就系統\n" +
                                "\n" +
                                "\uD83C\uDF1FV1.2更新(6/11/2019)\n" +
                                "-新增等級系統\n" +
                                "-增加更多探索選項\n" +
                                "-更改餓死既時間\n" +
                                "\n" +
                                "\uD83C\uDF1FV1.1更新(5/11/2019)\n" +
                                "-飢餓值會隨時間而減\n" +
                                "-每樣食物都有價錢，有錢先有得買\n" +
                                "-解決狗仔全伺服器同步問題\n" +
                                "\n" +
                                "\uD83C\uDF1FV1.0初行版(5/11/2019)\n" +
                                "-可以養一隻屬於自己的狗仔\n" +
                                "-可以同佢玩包剪揼\n" +
                                "-可以叫佢出去探索世界\n" +
                                "-可以餵佢食野");
                        try {
                            execute(message);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    if (command.equals("/feed")) {
                        if (dictionary.get(update.getMessage().getFrom().getId()).getDogCounter() > 0) {
                            if (checkHaveName(update)) {
                                String temp = getSpace(update.getMessage().getText());
                                message.setText("\uD83D\uDC15揀下想餵我食咩\n" +
                                        "\uD83E\uDD5B牛奶$10 飽食度:2\n" +
                                        "\uD83E\uDD63狗糧$15 飽食度:4\n" +
                                        "\uD83C\uDF56燒雞串$23 飽食度:15\n" +
                                        "\uD83E\uDD69肉$40 飽食度:12 HP:+10\n" +
                                        "\uD83C\uDF6B朱古力$150 飽食度:30\n" +
                                        "\uD83C\uDF6D波棒糖$200 飽食度:30 HP:+25\n" +
                                        "\uD83C\uDF82生日蛋糕$600 飽食度:75 HP:完全恢復\n" +
                                        "\uD83E\uDDEA微型生命恢復藥水$50 HP:+10\n" +
                                        "\uD83E\uDDEA中型生命恢復藥水$250 HP:+50\n" +
                                        "\uD83E\uDDEA大型生命恢復藥水$500 HP:+500\n" +
                                        "\uD83E\uDDEA完全恢復藥水$1000 HP:完全恢復\n");

                                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                                List<InlineKeyboardButton> rowInline = new ArrayList<>();
                                List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
                                List<InlineKeyboardButton> rowInline3 = new ArrayList<>();
                                rowInline.add(new InlineKeyboardButton().setText("牛奶").setCallbackData("food_milk"));
                                rowInline.add(new InlineKeyboardButton().setText("狗糧").setCallbackData("food_dogfood"));
                                rowInline.add(new InlineKeyboardButton().setText("肉").setCallbackData("food_meat"));
                                rowInline.add(new InlineKeyboardButton().setText("燒雞串").setCallbackData("food_chicken"));
                                rowInline1.add(new InlineKeyboardButton().setText("朱古力").setCallbackData("food_chocolate"));
                                rowInline1.add(new InlineKeyboardButton().setText("波棒糖").setCallbackData("food_lollipop"));
                                rowInline1.add(new InlineKeyboardButton().setText("生日蛋糕").setCallbackData("food_birthdaycake"));
                                rowInline1.add(new InlineKeyboardButton().setText("\uD83E\uDDEA藥水類").setCallbackData("food_potion"));

                                //hunger = dictionary.get(update.getMessage().getFrom().getId()).getHunger();
                                // Set the keyboard to the markup
                                rowsInline.add(rowInline);
                                rowsInline.add(rowInline1);
                                // Add it to the message
                                markupInline.setKeyboard(rowsInline);
                                message.setReplyMarkup(markupInline);
                            } else
                                message.setText("輸入/name [name] 幫我改過名先啦");
                        } else {
                            message.setText("輸入 /dog 去搵隻狗先啦");
                        }
                        try {
                            execute(message);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    if (command.equals("/arena")) {
                        if (dictionary.get(update.getMessage().getFrom().getId()).getDogCounter() > 0) {
                            if (checkHaveName(update)) {
                                message.setText("歡迎蒞臨競技埸\uD83D\uDDE1️\n\n" +
                                        "呢度係一個弱肉強食的世界，只要最勇猛的人先可以獲得勝利，" +
                                        "係競技埸入面，你可以遇到唔同的者敵" +
                                        "展開一埸又一埸的激烈的戰鬥。\n" +
                                        "勝利者可以獲得豐富的金錢，" +
                                        "而敗者只會成為強者的餌食\n" +
                                        "挑戰吧!少年!⚔️\n\n" +
                                        "\uD83C\uDF1F競技埸特色: \n" +
                                        "\t\t\t️🗡️只要血量係10以上，係競技埸係唔會死亡\n" +
                                        "\t\t\t🗡️️戰鬥不會獲得任何exp\n" +
                                        "\t\t\t🗡️️勝利者可以獲得大量金錢獎勵\n" +
                                        "\t\t\t🗡️️失敗者會損失大量的金錢\n" +
                                        "\t\t\t🗡️️達成一定條件，將會有特別獎勵\n\n" +
                                        "㩒一下 /combat 開始戰鬥啦");
                                dictionary.get(update.getMessage().getFrom().getId()).setInArena(true);
                            } else {
                                message.setText("輸入/name [name] 幫我改過名先啦");
                            }
                        } else {
                            message.setText("輸入 /dog 去搵隻狗先啦");
                        }
                        try {
                            execute(message);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    if (command.equals("/combat")) {
                        if (dictionary.get(update.getMessage().getFrom().getId()).isInArena()) {
                            message.setText("你要" + dictionary.get(update.getMessage().getFrom().getId()).getName() + "用咩類型的戰鬥風格?");
                            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                            List<InlineKeyboardButton> rowInline = new ArrayList<>();
                            rowInline.add(new InlineKeyboardButton().setText("快攻").setCallbackData("attack_fast"));
                            rowInline.add(new InlineKeyboardButton().setText("重攻擊").setCallbackData("attack_heavy"));
                            rowInline.add(new InlineKeyboardButton().setText("防守").setCallbackData("attack_defense"));
                            // Set the keyboard to the markup
                            rowsInline.add(rowInline);
                            // Add it to the message
                            markupInline.setKeyboard(rowsInline);
                            message.setReplyMarkup(markupInline);
                        }
                        else{
                            message.setText("你仲未進入競技埸，係咪想同空氣對打?\n" +
                                    "㩒一下呢度 /arena 啦");
                        }
                        try {
                            execute(message);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    if (command.equals("/shop")) {
                        if (dictionary.get(update.getMessage().getFrom().getId()).getDogCounter() > 0) {
                            if (checkHaveName(update)) {
                                String temp = getSpace(update.getMessage().getText());
                                message.setText("歡迎蒞臨商店\uD83C\uDFEA\n" +
                                        "\uD83D\uDC15係呢度買既野，會係儲存係物品庫\n" +
                                        "揀下想買啲咩\n" +
                                        "\uD83C\uDF6D波棒糖$200 飽食度:30 HP:+25\n" +
                                        "\uD83C\uDF82生日蛋糕$600 飽食度:75 HP:完全恢復\n" +
                                        "\uD83E\uDDEA微型生命恢復藥水$50 HP:+10\n" +
                                        "\uD83E\uDDEA中型生命恢復藥水$250 HP:+50\n" +
                                        "\uD83E\uDDEA大型生命恢復藥水$500 HP:+500\n" +
                                        "\uD83E\uDDEA完全恢復藥水$1000 HP:完全恢復\n");

                                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                                List<InlineKeyboardButton> rowInline = new ArrayList<>();
                                List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
                                List<InlineKeyboardButton> rowInline3 = new ArrayList<>();
                                rowInline1.add(new InlineKeyboardButton().setText("波棒糖").setCallbackData("shop_food_lollipop"));
                                rowInline1.add(new InlineKeyboardButton().setText("生日蛋糕").setCallbackData("shop_food_birthdaycake"));
                                rowInline1.add(new InlineKeyboardButton().setText("\uD83E\uDDEA藥水類").setCallbackData("shop_food_potion"));

                                //hunger = dictionary.get(update.getMessage().getFrom().getId()).getHunger();
                                // Set the keyboard to the markup
                                rowsInline.add(rowInline);
                                rowsInline.add(rowInline1);
                                // Add it to the message
                                markupInline.setKeyboard(rowsInline);
                                message.setReplyMarkup(markupInline);
                            } else
                                message.setText("輸入/name [name] 幫我改過名先啦");
                        } else {
                            message.setText("輸入 /dog 去搵隻狗先啦");
                        }
                        try {
                            execute(message);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    //當玩家輸入/kill
                    if (command.equals("/kill")) {
                        if (dictionary.get(update.getMessage().getFrom().getId()).getDogCounter() > 0) {
                            message.setText("你係咪要火葬你隻狗 QAQ?");
                            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                            List<InlineKeyboardButton> rowInline = new ArrayList<>();
                            List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
                            rowInline.add(new InlineKeyboardButton().setText("Yes").setCallbackData("kill_yes"));
                            rowInline.add(new InlineKeyboardButton().setText("No").setCallbackData("kill_no"));
                            // Set the keyboard to the markup
                            rowsInline.add(rowInline);
                            // Add it to the message
                            markupInline.setKeyboard(rowsInline);
                            message.setReplyMarkup(markupInline);
                        } else {
                            message.setText("輸入 /dog 去搵隻狗先啦");
                        }
                        try {
                            execute(message);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    if (command.equals("printID")) {
                        System.out.println(update.getMessage().getFrom().getId());
                    }
                    if (command.equals("/play")) {
                        String temp = getSpace(update.getMessage().getText());
                        if (dictionary.get(update.getMessage().getFrom().getId()).getDogCounter() > 0) {
                            if (checkHaveName(update)) {
                                message.setText("想同我玩咩遊戲?");
                                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                                List<InlineKeyboardButton> rowInline = new ArrayList<>();
                                List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
                                rowInline.add(new InlineKeyboardButton().setText("1.包剪揼").setCallbackData("game_one"));
                                rowInline.add(new InlineKeyboardButton().setText("2. 找鑽石").setCallbackData("game_two"));
                                // Set the keyboard to the markup
                                rowsInline.add(rowInline);
                                // Add it to the message
                                markupInline.setKeyboard(rowsInline);
                                message.setReplyMarkup(markupInline);
                            } else
                                message.setText("輸入/name [name] 幫我改過名先啦");
                        } else
                            message.setText("輸入 /dog 去搵隻狗先啦");
                        try {
                            execute(message);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    //當玩家輸入/explore
                    if (command.equals("/explore")) {
                        String explore_msg = "";
                        int[] seconds = {1, 1, 1};
                        int random = (int) (Math.random() * (2 - 0 + 1) + 0);
                        System.out.println("random: " + random);
                        if (dictionary.get(update.getMessage().getFrom().getId()).getDogCounter() > 0) {
                            if (checkHaveName(update)) {
                                message.setText("\uD83D\uDC3E" + dictionary.get(update.getMessage().getFrom().getId()).getName() + "去左探索世界\uD83D\uDDFC" + seconds[random] + "秒");
                                dictionary.get(update.getMessage().getFrom().getId()).setExploring(true);
                                dictionary.get(update.getMessage().getFrom().getId()).setInArena(false);
                                setTimer(seconds[random], update);
                                try {
                                    execute(message);
                                } catch (TelegramApiException e) {
                                    e.printStackTrace();
                                }

                            } else {
                                message.setText("輸入/name [name] 幫我改過名先啦");
                                try {
                                    execute(message);
                                } catch (TelegramApiException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            message.setText("輸入 /dog 去搵隻狗先啦");
                            try {
                                execute(message);
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    if (command.equals(("admin setDogData"))) {
                        String temp = getSpace(update.getMessage().getText());
                        if (dictionary.get(update.getMessage().getFrom().getId()).getDogCounter() > 0) {
                            if (checkHaveName(update)) {
                                message.setText("想設定咩?");
                                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                                List<InlineKeyboardButton> rowInline = new ArrayList<>();
                                List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
                                rowInline.add(new InlineKeyboardButton().setText("Relationship").setCallbackData("setDogData_relationship"));
                                rowInline.add(new InlineKeyboardButton().setText("Currency").setCallbackData("setDogData_currency"));
                                rowInline.add(new InlineKeyboardButton().setText("Level").setCallbackData("setDogData_level"));
                                // Set the keyboard to the markup
                                rowsInline.add(rowInline);
                                // Add it to the message
                                markupInline.setKeyboard(rowsInline);
                                message.setReplyMarkup(markupInline);
                            } else
                                message.setText("輸入/name [name] 幫我改過名先啦");
                        } else
                            message.setText("輸入 /dog 去搵隻狗先啦");
                        try {
                            execute(message);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }

                    /*if (command.equals("admin clear")) {
                        dictionary.clear();
                    }*/
                    if (command.equals("admin printhash")) {
                        for (int temp01 : dictionary.keySet()) {
                            System.out.println(temp01);
                        }
                    }
                    if (dictionary.get(update.getMessage().getFrom().getId()).isToggle_settingRelationship()) {
                        if (Integer.parseInt(update.getMessage().getText()) >= 0 && Integer.parseInt(update.getMessage().getText()) <= 100) {
                            dictionary.get(update.getMessage().getFrom().getId()).setRelationship(Integer.parseInt(update.getMessage().getText()));
                            dictionary.get(update.getMessage().getFrom().getId()).setToggle_settingRelationship(false);
                        } else
                            dictionary.get(update.getMessage().getFrom().getId()).setToggle_settingRelationship(false);
                    }
                    if (dictionary.get(update.getMessage().getFrom().getId()).isToggle_settingCurrency()) {
                        if (Integer.parseInt(update.getMessage().getText()) >= 0) {
                            dictionary.get(update.getMessage().getFrom().getId()).setCurrency(Integer.parseInt(update.getMessage().getText()));
                            dictionary.get(update.getMessage().getFrom().getId()).setToggle_settingCurrency(false);
                        }
                        dictionary.get(update.getMessage().getFrom().getId()).setToggle_settingCurrency(false);

                    }
                    if (dictionary.get(update.getMessage().getFrom().getId()).isToggle_settingLevel()) {
                        if (Integer.parseInt(update.getMessage().getText()) >= 1 && Integer.parseInt(update.getMessage().getText()) <=8) {
                            dictionary.get(update.getMessage().getFrom().getId()).setLevel(Integer.parseInt(update.getMessage().getText()));
                            dictionary.get(update.getMessage().getFrom().getId()).setMaximumHealth(addHP(update));
                            dictionary.get(update.getMessage().getFrom().getId()).setToggle_settingLevel(false);
                        }
                        dictionary.get(update.getMessage().getFrom().getId()).setToggle_settingLevel(false);
                    }

                } else {
                    String command = update.getMessage().getText();
                    SendMessage message = new SendMessage();
                    message.setChatId(update.getMessage().getChatId());
                    message.setText("\uD83D\uDC3E" + dictionary.get(update.getMessage().getFrom().getId()).getName() + "係度探索緊世界\uD83D\uDDFC轉頭再搵佢啦");
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                String command = update.getMessage().getText();
                SendMessage message = new SendMessage();
                message.setChatId(update.getMessage().getChatId());
                message.setText("\uD83D\uDC3E" + dictionary.get(update.getMessage().getFrom().getId()).getName() + "已經死左\uD83D\uDC80");
                dictionary.remove(update.getMessage().getFrom().getId());
                //removeAllData();
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        } else if (update.hasCallbackQuery()) {
            // Set variables
            userID = update.getCallbackQuery().getFrom().getId();
            String call_data = update.getCallbackQuery().getData();
            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            long chat_id = update.getCallbackQuery().getMessage().getChatId();

            EditMessageText new_message = new EditMessageText()
                    .setChatId(chat_id)
                    .setMessageId(toIntExact(message_id));
            if (call_data.equals("food_milk")) {
                //System.out.println(update.getCallbackQuery().getFrom().getId());
                if (!(dictionary.get(update.getCallbackQuery().getFrom().getId()).getHunger() + 2 > 100)) {
                    if (dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() >= 10) {
                        new_message.setText("用$10餵左牛奶\n飢餓值增加左: 2");
                        dictionary.get(update.getCallbackQuery().getFrom().getId()).setHunger(dictionary.get(update.getCallbackQuery().getFrom().getId()).getHunger() + 2);
                        dictionary.get(update.getCallbackQuery().getFrom().getId()).setCurrency(dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() - 10);
                    } else {
                        new_message.setText(("你無足夠的錢 "));
                    }
                } else {
                    new_message.setText("\uD83D\uDC3E" + dictionary.get(update.getCallbackQuery().getFrom().getId()).getName() + "太飽了，遲D再餵佢食野啦");
                }
            }
            if (call_data.equals("food_dogfood")) {
                if (!(dictionary.get(update.getCallbackQuery().getFrom().getId()).getHunger() + 4 > 100)) {
                    if (dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() >= 15) {
                        new_message.setText("用$15餵左狗糧\n飢餓值增加左: 4");
                        dictionary.get(update.getCallbackQuery().getFrom().getId()).setHunger(dictionary.get(update.getCallbackQuery().getFrom().getId()).getHunger() + 4);
                        dictionary.get(update.getCallbackQuery().getFrom().getId()).setCurrency(dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() - 15);
                    } else {
                        new_message.setText(("你無足夠的錢"));
                    }
                } else {
                    new_message.setText("\uD83D\uDC3E" + dictionary.get(update.getCallbackQuery().getFrom().getId()).getName() + "太飽了，遲D再餵佢食野啦");
                }
            }
            if (call_data.equals("food_chicken")) {
                if (!(dictionary.get(update.getCallbackQuery().getFrom().getId()).getHunger() + 15 > 100)) {
                    if (dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() >= 23) {
                        new_message.setText("用$40餵左雞肉串\n飢餓值增加左: 15");
                        dictionary.get(update.getCallbackQuery().getFrom().getId()).setHunger(dictionary.get(update.getCallbackQuery().getFrom().getId()).getHunger() + 15);
                        dictionary.get(update.getCallbackQuery().getFrom().getId()).setCurrency(dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() - 23);
                    } else {
                        new_message.setText(("你無足夠的錢"));
                    }
                } else {
                    new_message.setText("\uD83D\uDC3E" + dictionary.get(update.getCallbackQuery().getFrom().getId()).getName() + "太飽了，遲D再餵佢食野啦");
                }
            }
            if (call_data.equals("food_meat")) {
                if (!(dictionary.get(update.getCallbackQuery().getFrom().getId()).getHunger() + 12 > 100)) {
                    if (dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() >= 40) {
                        new_message.setText("用$40餵左肉\n飢餓值增加左: 12\nHP恢復左10");
                        dictionary.get(update.getCallbackQuery().getFrom().getId()).setHunger(dictionary.get(update.getCallbackQuery().getFrom().getId()).getHunger() + 12);
                        dictionary.get(update.getCallbackQuery().getFrom().getId()).setCurrency(dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() - 40);
                        if ((dictionary.get(update.getCallbackQuery().getFrom().getId()).getHealth() + 10) > dictionary.get(update.getCallbackQuery().getFrom().getId()).getMaximumHealth()) {
                            dictionary.get(update.getCallbackQuery().getFrom().getId()).setHealth(dictionary.get(update.getCallbackQuery().getFrom().getId()).getMaximumHealth()); //set最大生命值
                        } else {
                            dictionary.get(update.getCallbackQuery().getFrom().getId()).setHealth(dictionary.get(update.getCallbackQuery().getFrom().getId()).getHealth() + 10);
                        }
                    } else {
                        new_message.setText(("你無足夠的錢"));
                    }
                } else {
                    new_message.setText("\uD83D\uDC3E" + dictionary.get(update.getCallbackQuery().getFrom().getId()).getName() + "太飽了，遲D再餵佢食野啦");
                }
            }
            if (call_data.equals("food_chocolate")) {
                if (!(dictionary.get(update.getCallbackQuery().getFrom().getId()).getHunger() + 30 > 100)) {
                    if (dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() >= 150) {
                        new_message.setText("用$150餵左朱古力\n飢餓值增加左: 30");
                        dictionary.get(update.getCallbackQuery().getFrom().getId()).setHunger(dictionary.get(update.getCallbackQuery().getFrom().getId()).getHunger() + 30);
                        dictionary.get(update.getCallbackQuery().getFrom().getId()).setCurrency(dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() - 150);
                    } else {
                        new_message.setText(("你無足夠的錢"));
                    }
                } else {
                    new_message.setText("\uD83D\uDC3E" + dictionary.get(update.getCallbackQuery().getFrom().getId()).getName() + "會飽到死嫁");
                }
            }
            if (call_data.equals("food_lollipop")) {
                if (dictionary.get(update.getCallbackQuery().getFrom().getId()).getInventory().get("波板糖")> 0) {
                    if (!(dictionary.get(update.getCallbackQuery().getFrom().getId()).getHunger() + 30 > 100)) {
                        new_message.setText("餵左物品庫裹面的波板糖\n");
                        dictionary.get(update.getCallbackQuery().getFrom().getId()).getInventory().put("波板糖", dictionary.get(update.getCallbackQuery().getFrom().getId()).getInventory().get("波板糖") - 1);
                    } else {
                        new_message.setText("\uD83D\uDC3E" + dictionary.get(update.getCallbackQuery().getFrom().getId()).getName() + "會飽到死嫁");
                    }
                }else if (!(dictionary.get(update.getCallbackQuery().getFrom().getId()).getHunger() + 30 > 100)) {
                    if (dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() >= 200) {
                        new_message.setText("用$200餵左波板糖\n飢餓值增加左: 30\nHP恢復左25");
                        dictionary.get(update.getCallbackQuery().getFrom().getId()).setHunger(dictionary.get(update.getCallbackQuery().getFrom().getId()).getHunger() + 30);
                        dictionary.get(update.getCallbackQuery().getFrom().getId()).setCurrency(dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() - 200);
                        //check下最大生命值
                        if ((dictionary.get(update.getCallbackQuery().getFrom().getId()).getHealth() + 25) > dictionary.get(update.getCallbackQuery().getFrom().getId()).getMaximumHealth()) {
                            dictionary.get(update.getCallbackQuery().getFrom().getId()).setHealth(dictionary.get(update.getCallbackQuery().getFrom().getId()).getMaximumHealth()); //set最大生命值
                        } else {
                            dictionary.get(update.getCallbackQuery().getFrom().getId()).setHealth(dictionary.get(update.getCallbackQuery().getFrom().getId()).getHealth() + 25);
                        }
                    } else {
                        new_message.setText(("你無足夠的錢"));
                    }
                } else {
                    new_message.setText("\uD83D\uDC3E" + dictionary.get(update.getCallbackQuery().getFrom().getId()).getName() + "會飽到死嫁");
                }
            }

            if (call_data.equals("food_birthdaycake")) {
                if (dictionary.get(update.getCallbackQuery().getFrom().getId()).getInventory().get("生日蛋糕")> 0){
                    if (!(dictionary.get(update.getCallbackQuery().getFrom().getId()).getHunger() + 75 > 100)) {
                        new_message.setText("餵左物品庫裹面的生日蛋糕\n");
                        dictionary.get(update.getCallbackQuery().getFrom().getId()).getInventory().put("波板糖",dictionary.get(update.getCallbackQuery().getFrom().getId()).getInventory().get("波板糖") - 1 );
                    }else {
                        new_message.setText("\uD83D\uDC3E" + dictionary.get(update.getCallbackQuery().getFrom().getId()).getName() + "會飽到死嫁");
                    }
                } else if (!(dictionary.get(update.getCallbackQuery().getFrom().getId()).getHunger() + 75 > 100)) {
                    if (dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() >= 600) {
                        new_message.setText("用$600餵左生日蛋糕\n飢餓值增加左: 75\nHP完全恢復");
                        dictionary.get(update.getCallbackQuery().getFrom().getId()).setHunger(dictionary.get(update.getCallbackQuery().getFrom().getId()).getHunger() + 75);
                        dictionary.get(update.getCallbackQuery().getFrom().getId()).setCurrency(dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() - 600);
                        dictionary.get(update.getCallbackQuery().getFrom().getId()).setHealth(dictionary.get(update.getCallbackQuery().getFrom().getId()).getMaximumHealth()); //set最大生命值
                    } else {
                        new_message.setText(("你無足夠的錢"));
                    }

                } else {
                    new_message.setText("\uD83D\uDC3E" + dictionary.get(update.getCallbackQuery().getFrom().getId()).getName() + "會飽到死嫁");
                }

            }
            if (call_data.equals("food_potion")) {
                new_message.setText("\uD83E\uDDEA微型生命恢復藥水$50 HP:+10\n" +
                                    "\uD83E\uDDEA中型生命恢復藥水$250 HP:+50\n" +
                                    "\uD83E\uDDEA大型生命恢復藥水$500 HP:+500\n" +
                                    "\uD83E\uDDEA完全恢復藥水$1000 HP:完全恢復\n");
                    InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                    List<InlineKeyboardButton> rowInline = new ArrayList<>();
                    List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
                    rowInline.add(new InlineKeyboardButton().setText("微型").setCallbackData("potion_small"));
                    rowInline.add(new InlineKeyboardButton().setText("中型").setCallbackData("potion_medium"));
                    rowInline1.add(new InlineKeyboardButton().setText("大型").setCallbackData("potion_large"));
                    rowInline1.add(new InlineKeyboardButton().setText("完全恢復").setCallbackData("potion_full"));
                    // Set the keyboard to the markup
                    rowsInline.add(rowInline);
                    rowsInline.add(rowInline1);
                    // Add it to the message
                    markupInline.setKeyboard(rowsInline);
                    new_message.setReplyMarkup(markupInline);
            }
            //藥水類
            if (call_data.equals("potion_small")){
                if (dictionary.get(update.getCallbackQuery().getFrom().getId()).getInventory().get("生日蛋糕")> 0){
                    if (!(dictionary.get(update.getCallbackQuery().getFrom().getId()).getHunger() + 75 > 100)) {
                        new_message.setText("餵左物品庫裹面的生日蛋糕\n");
                        dictionary.get(update.getCallbackQuery().getFrom().getId()).getInventory().put("生日蛋糕",dictionary.get(update.getCallbackQuery().getFrom().getId()).getInventory().get("生日蛋糕") - 1 );
                    }else {
                        new_message.setText("\uD83D\uDC3E" + dictionary.get(update.getCallbackQuery().getFrom().getId()).getName() + "會飽到死嫁");
                    }
                    if (dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() >= 50) {
                        new_message.setText("用$50買左微型生命恢復藥水\nHP恢復左10");
                        dictionary.get(update.getCallbackQuery().getFrom().getId()).setHealth(dictionary.get(update.getCallbackQuery().getFrom().getId()).getHealth() + 10);
                        dictionary.get(update.getCallbackQuery().getFrom().getId()).setCurrency(dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() - 50);

                    } else {
                        new_message.setText(("你無足夠的錢"));
                    }
            }
            if (call_data.equals("potion_medium")){
                if (dictionary.get(update.getCallbackQuery().getFrom().getId()).getInventory().get("中型生命恢復藥")> 0){
                        new_message.setText("餵左物品庫裹面的中型生命恢復藥\n");
                    dictionary.get(update.getCallbackQuery().getFrom().getId()).getInventory().put("中型生命恢復藥",dictionary.get(update.getCallbackQuery().getFrom().getId()).getInventory().get("中型生命恢復藥") - 1 );
                    }
                    else if (dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() >= 250) {
                    new_message.setText("用$250買左中型生命恢復藥水\nHP恢復左50");
                    dictionary.get(update.getCallbackQuery().getFrom().getId()).setHealth(dictionary.get(update.getCallbackQuery().getFrom().getId()).getHealth() + 50);
                    dictionary.get(update.getCallbackQuery().getFrom().getId()).setCurrency(dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() - 250);
                } else {
                    new_message.setText(("你無足夠的錢"));
                }
            }
            if (call_data.equals("potion_large")){
                if (dictionary.get(update.getCallbackQuery().getFrom().getId()).getInventory().get("大型生命恢復藥水")>0){
                        new_message.setText("餵左物品庫裹面的大型生命恢復藥水\n");
                    dictionary.get(update.getCallbackQuery().getFrom().getId()).getInventory().put("大型生命恢復藥水",dictionary.get(update.getCallbackQuery().getFrom().getId()).getInventory().get("大型生命恢復藥水") - 1 );
                    }
            }else if (dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() >= 500) {
                    new_message.setText("用$500買左大型生命恢復藥水\nHP恢復左500");
                    dictionary.get(update.getCallbackQuery().getFrom().getId()).setHealth(dictionary.get(update.getCallbackQuery().getFrom().getId()).getHealth() + 500);
                    dictionary.get(update.getCallbackQuery().getFrom().getId()).setCurrency(dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() - 500);

                } else {
                    new_message.setText(("你無足夠的錢"));
                }
            }
            if (call_data.equals("potion_full")){
                if (dictionary.get(update.getCallbackQuery().getFrom().getId()).getInventory().get("完全恢復藥水")> 0){
                        new_message.setText("餵左物品庫裹面的完全恢復藥水\n");
                    dictionary.get(update.getCallbackQuery().getFrom().getId()).getInventory().put("完全恢復藥水",dictionary.get(update.getCallbackQuery().getFrom().getId()).getInventory().get("完全恢復藥水") - 1 );
            }else if (dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() >= 1000) {
                    new_message.setText("用$1000買左完全恢復藥水\nHP完全恢復");
                    dictionary.get(update.getCallbackQuery().getFrom().getId()).setHealth(dictionary.get(update.getCallbackQuery().getFrom().getId()).getMaximumHealth());
                    dictionary.get(update.getCallbackQuery().getFrom().getId()).setCurrency(dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() - 1000);
                } else {
                    new_message.setText(("你無足夠的錢"));
                }
            }
            //Shop 店鋪類
            if (call_data.equals("shop_food_lollipop")) {
                    if (dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() >= 200) {
                        new_message.setText("用$200買左波板糖\n");
                        dictionary.get(update.getCallbackQuery().getFrom().getId()).setCurrency(dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() - 200);
                        try {
                            dictionary.get(update.getCallbackQuery().getFrom().getId()).getInventory().put("波板糖", dictionary.get(update.getCallbackQuery().getFrom().getId()).getInventory().get("波板糖") + 1);
                        }catch(NullPointerException e){
                            dictionary.get(update.getCallbackQuery().getFrom().getId()).getInventory().put("波板糖",1);
                        }
                    } else {
                        new_message.setText(("你無足夠的錢"));
                    }
            }

            if (call_data.equals("shop_food_birthdaycake")) {
                    if (dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() >= 600) {
                        new_message.setText("用$600買左生日蛋糕\n");
                        dictionary.get(update.getCallbackQuery().getFrom().getId()).setCurrency(dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() - 600);
                        try {
                            dictionary.get(update.getCallbackQuery().getFrom().getId()).getInventory().put("生日蛋糕", dictionary.get(update.getCallbackQuery().getFrom().getId()).getInventory().get("生日蛋糕") + 1);
                        }catch(NullPointerException e){
                            dictionary.get(update.getCallbackQuery().getFrom().getId()).getInventory().put("生日蛋糕", 1);
                        }
                    } else {
                        new_message.setText(("你無足夠的錢"));
                    }

            }
            if (call_data.equals("shop_food_potion")) {
                new_message.setText("\uD83E\uDDEA微型生命恢復藥水$50 HP:+10\n" +
                        "\uD83E\uDDEA中型生命恢復藥水$250 HP:+50\n" +
                        "\uD83E\uDDEA大型生命恢復藥水$500 HP:+500\n" +
                        "\uD83E\uDDEA完全恢復藥水$1000 HP:完全恢復\n");
                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                List<InlineKeyboardButton> rowInline = new ArrayList<>();
                List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
                rowInline.add(new InlineKeyboardButton().setText("微型").setCallbackData("shop_potion_small"));
                rowInline.add(new InlineKeyboardButton().setText("中型").setCallbackData("shop_potion_medium"));
                rowInline1.add(new InlineKeyboardButton().setText("大型").setCallbackData("shop_potion_large"));
                rowInline1.add(new InlineKeyboardButton().setText("完全恢復").setCallbackData("shop_potion_full"));
                // Set the keyboard to the markup
                rowsInline.add(rowInline);
                rowsInline.add(rowInline1);
                // Add it to the message
                markupInline.setKeyboard(rowsInline);
                new_message.setReplyMarkup(markupInline);
            }
            //藥水類
            if (call_data.equals("shop_potion_small")){
                if (dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() >= 50) {
                    new_message.setText("用$50買左微型生命恢復藥水\n");
                    dictionary.get(update.getCallbackQuery().getFrom().getId()).setCurrency(dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() - 50);
                    try {
                        dictionary.get(update.getCallbackQuery().getFrom().getId()).getInventory().put("微型生命恢復藥水", dictionary.get(update.getCallbackQuery().getFrom().getId()).getInventory().get("微型生命恢復藥水") + 1);
                    }catch(NullPointerException e){
                        dictionary.get(update.getCallbackQuery().getFrom().getId()).getInventory().put("微型生命恢復藥水", 1);
                    }

                } else {
                    new_message.setText(("你無足夠的錢"));
                }
            }
            if (call_data.equals("shop_potion_medium")){
                if (dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() >= 250) {
                    new_message.setText("用$250買左中型生命恢復藥水\n");
                    dictionary.get(update.getCallbackQuery().getFrom().getId()).setCurrency(dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() - 250);
                    try {
                        dictionary.get(update.getCallbackQuery().getFrom().getId()).getInventory().put("中型生命恢復藥水", dictionary.get(update.getCallbackQuery().getFrom().getId()).getInventory().get("中型生命恢復藥水") + 1);
                    }catch(NullPointerException e){
                        dictionary.get(update.getCallbackQuery().getFrom().getId()).getInventory().put("中型生命恢復藥水", 1);
                    }

                } else {
                    new_message.setText(("你無足夠的錢"));
                }
            }
            if (call_data.equals("shop_potion_large")){
                if (dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() >= 500) {
                    new_message.setText("用$500買左大型生命恢復藥水\n");
                    dictionary.get(update.getCallbackQuery().getFrom().getId()).setCurrency(dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() - 500);
                    try {
                        dictionary.get(update.getCallbackQuery().getFrom().getId()).getInventory().put("大型生命恢復藥水", dictionary.get(update.getCallbackQuery().getFrom().getId()).getInventory().get("大型生命恢復藥水") + 1);
                    }catch(NullPointerException e){
                        dictionary.get(update.getCallbackQuery().getFrom().getId()).getInventory().put("大型生命恢復藥水", 1);
                    }
                } else {
                    new_message.setText(("你無足夠的錢"));
                }
            }
            if (call_data.equals("shop_potion_full")){
                if (dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() >= 1000) {
                    new_message.setText("用$1000買左完全恢復藥水\n");
                    dictionary.get(update.getCallbackQuery().getFrom().getId()).setCurrency(dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() - 1000);
                    try {
                        dictionary.get(update.getCallbackQuery().getFrom().getId()).getInventory().put("完全恢復藥水", dictionary.get(update.getCallbackQuery().getFrom().getId()).getInventory().get("完全恢復藥水") + 1);
                    }catch(NullPointerException e){
                        dictionary.get(update.getCallbackQuery().getFrom().getId()).getInventory().put("完全恢復藥水", 1);
                    }
                } else {
                    new_message.setText(("你無足夠的錢"));
                }
            }
            //如果玩家決定殺狗
            if (call_data.equals("kill_yes")) {
                new_message.setText("\uD83D\uDC3E" + dictionary.get(update.getCallbackQuery().getFrom().getId()).getName() + "已經前往西天了\uD83D\uDC80 R.I.P.");
                dictionary.remove(update.getCallbackQuery().getFrom().getId());
                for (int temp : dictionary.keySet()) {
                    System.out.println(temp);
                }
                System.out.println("Print Complete");
            }
            //如果玩家唔殺狗
            if (call_data.equals("kill_no")) {
                new_message.setText("多謝你唔殺我\uD83D\uDE2D\uD83D\uDE2D\uD83D\uDE2D");
            }
            String result1 = "";
            if (call_data.equals("game_one")) {
                new_message.setText("你想出咩?");
                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                List<InlineKeyboardButton> rowInline = new ArrayList<>();
                rowInline.add(new InlineKeyboardButton().setText("\uD83D\uDD90包").setCallbackData("paper"));
                rowInline.add(new InlineKeyboardButton().setText("✌剪").setCallbackData("scissors"));
                rowInline.add(new InlineKeyboardButton().setText("\uD83D\uDC4A揼").setCallbackData("stone"));

                // Set the keyboard to the markup
                rowsInline.add(rowInline);
                // Add it to the message
                markupInline.setKeyboard(rowsInline);
                new_message.setReplyMarkup(markupInline);

            }
            String result2 = "";
            if (call_data.equals("game_two")) {
                new_message.setText("你面前有3個行李箱\uD83E\uDDF3\n其中一個行李箱裝著一粒\uD83D\uDC8E\n" +
                        "粒鑽石係邊個箱到?");
                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                List<InlineKeyboardButton> rowInline = new ArrayList<>();
                rowInline.add(new InlineKeyboardButton().setText("左").setCallbackData("left"));
                rowInline.add(new InlineKeyboardButton().setText("中").setCallbackData("middle"));
                rowInline.add(new InlineKeyboardButton().setText("右").setCallbackData("right"));

                // Set the keyboard to the markup
                rowsInline.add(rowInline);
                // Add it to the message
                markupInline.setKeyboard(rowsInline);
                new_message.setReplyMarkup(markupInline);

            }
            //Game 1
            if (call_data.equals("paper")) {
                result1 = playGame1(1, update);
                new_message.setText(result1);
            }
            if (call_data.equals("scissors")) {
                result1 = playGame1(2, update);
                new_message.setText(result1);
            }

            if (call_data.equals("stone")) {
                result1 = playGame1(3, update);
                new_message.setText(result1);
            }
            //Game 2
            if (call_data.equals("left")) {
                result2 = playGame2(1, update);
                new_message.setText(result2);
            }
            if (call_data.equals("middle")) {
                result2 = playGame2(2, update);
                new_message.setText(result2);
            }

            if (call_data.equals("right")) {
                result2 = playGame2(3, update);
                new_message.setText(result2);
            }
            String result3 = "";
            //Game 3
            if (call_data.equals("attack_fast")) {
                result3 = playCombat(0, update);
                new_message.setText(result3);
            }
            if (call_data.equals("attack_heavy")) {
                result3 = playCombat(1, update);
                new_message.setText(result3);
            }

            if (call_data.equals("attack_defense")) {
                result3 = playCombat(2, update);
                new_message.setText(result3);
            }
            if (call_data.equals("setDogData_relationship")) {
                new_message.setText("輸入你想設定的數字(0-100)");
                dictionary.get(update.getCallbackQuery().getFrom().getId()).setToggle_settingRelationship(true);
            }
            if (call_data.equals("setDogData_currency")) {
                new_message.setText(("輸入你想設定的數字(>=0)"));
                dictionary.get(update.getCallbackQuery().getFrom().getId()).setToggle_settingCurrency(true);
            }
            if (call_data.equals("setDogData_level")) {
                new_message.setText(("輸入你想設定的數字(>=1 <=8)"));
                dictionary.get(update.getCallbackQuery().getFrom().getId()).setToggle_settingLevel(true);
            }

            try {
                execute(new_message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        try {
                checkAchievements(update);
        } catch (NullPointerException e) {
        }
    }


    public String getBotUsername() {
        // TODO
        return "PlayWithdoggyTest";
        //Online    :PlayWithDoggy
        //Test      :PlayWithdoggyTest
    }

    @Override
    public String getBotToken() {
        // TODO
        return "844161272:AAFSfCUKlWHCukJhjAjzjDze577oYcvdj3k";
        //Online :"844161272:AAFSfCUKlWHCukJhjAjzjDze577oYcvdj3k";
        //Test   :"1032223034:AAFcHuiTtpTcB2_9jpMw9V-TGbhsfHC-RuY";
    }
}
