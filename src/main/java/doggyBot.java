import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

import static java.lang.Math.toIntExact;


public class doggyBot extends TelegramLongPollingBot {
    private HashMap<Integer, Dog> dictionary = new HashMap<>();
    public  HashMap<Integer, Integer> growCurve = new HashMap<>();
    public HashMap<String, String> totalAchievements = new HashMap<>();


    public void initalizeGrowCurve() {
        growCurve.put(1,12);
        growCurve.put(2,24);
        growCurve.put(3,36);
        growCurve.put(4,48);
        growCurve.put(5,96);
        growCurve.put(6,192);
        growCurve.put(7,384);
        growCurve.put(8,768);
    }
    public void initalizeTotalAchievements(){
        totalAchievements.put("愛狗之人","和狗隻親密度達到30");
        totalAchievements.put("狗的好夥伴","和狗隻親密度達至50");
        totalAchievements.put("你前世係狗","和狗隻親密度達至100");
        totalAchievements.put("多些一舉","狗隻生命值全滿時幫他補血");
        totalAchievements.put("病態賭徒","在競技埸挑戰10次");
        totalAchievements.put("新手上路","命令狗隻前往探險");
        totalAchievements.put("殺狗狂徒","曾經企圖殺狗");
        totalAchievements.put("大富翁","彩產超過$2500");

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
        int hp = dictionary.get(update.getMessage().getFrom().getId()).getHealth();
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
            return 2;
        }
        else if (currentLevel == 3){
            return 2;
        }
        else if (currentLevel == 4){
            return 4;
        }
        else if (currentLevel == 5){
            return 6;
        }
        else if (currentLevel == 6){
            return 5;
        }
        else if (currentLevel == 7){
            return 5;
        }
        else
            return 3;
    }
    public void checkExpLevelUp(Update update){
        String command = update.getMessage().getText();
        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId());

        if (dictionary.get(update.getMessage().getFrom().getId()).getExp()>= growCurve.get(dictionary.get(update.getMessage().getFrom().getId()).getLevel())){
            dictionary.get(update.getMessage().getFrom().getId()).setLevel(dictionary.get(update.getMessage().getFrom().getId()).getLevel() + 1);
            message.setText("你隻狗Level Up⬆️\n佢而家係LV"+dictionary.get(update.getMessage().getFrom().getId()).getLevel()+
                    "\n佢既最大生命值提升左"+ addHP(update)+"\uD83C\uDF89");
            dictionary.get(update.getMessage().getFrom().getId()).setMaximumHealth(dictionary.get(update.getMessage().getFrom().getId()).getMaximumHealth() + addHP(update));
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

    }
    public void checkAchievements(Update update){
        int userID = 0;
        SendMessage message = new SendMessage();
        try {
            message.setChatId(update.getMessage().getChatId());
            userID = update.getMessage().getFrom().getId();
        }catch (NullPointerException e){
            message.setChatId(update.getCallbackQuery().getMessage().getChatId());
            userID = update.getCallbackQuery().getFrom().getId();
        }
        if(!dictionary.get(userID).getAchievedAchievements().contains("愛狗之人")) {
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
        if(!dictionary.get(userID).getAchievedAchievements().contains("大富翁")) {
            if (dictionary.get(userID).getCurrency() >= 2500) {
                dictionary.get(userID).getAchievedAchievements().add("大富翁");
                message.setText("\uD83C\uDF89\uD83C\uDF89你已解鎖成就\n「大富翁」");
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
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

    public String exploreTheWorld(Update update){
        String result ="";

        int randomStatement = (int) (Math.random() * (12-0+1)+0);
        if (randomStatement == 0){
            int cash = (int) (Math.random() * (50-1+1)+1);
            result += "搵到一個寶箱\uD83C\uDF81\n";
            result += "打開後有$" +cash;
            dictionary.get(update.getMessage().getFrom().getId()).setCurrency(dictionary.get(update.getMessage().getFrom().getId()).getCurrency()+cash);
        }
        if (randomStatement == 1){
            result += "發現一個新地點\uD83D\uDDFB\n";
        }
        if (randomStatement == 2){
            int cash = (int) (Math.random() * (50-1+1)+1);
            result += "係地下執到錢\uD83D\uDCB0\n";
            result+= "有$"+ cash;
            dictionary.get(update.getMessage().getFrom().getId()).setCurrency(dictionary.get(update.getMessage().getFrom().getId()).getCurrency()+cash);
        }
        if (randomStatement == 3){
            int cash = (int) (Math.random() * (80-30+1)+30);
            int win = (int) (Math.random() * (1-0+1)+0);
            int loss = (int) (Math.random() * (12-2+1)+2);
            int exp = (int) (Math.random() * (12 -1 + 1)+ 1);
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
            int exp = (int) (Math.random() * (20 -1 + 1)+ 1);
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
            int loss = (int) (Math.random() * (12-2+1)+2);
            int exp = (int) (Math.random() * (12 -1 + 1)+ 1);
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
            int loss = (int) (Math.random() * (12-2+1)+2);
            int exp = (int) (Math.random() * (12 -1 + 1)+ 1);
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
            int loss = (int) (Math.random() * (12-2+1)+2);
            int exp = (int) (Math.random() * (12 -1 + 1)+ 1);
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
                        message.setText("輸入/dog去搵一隻狗");
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
                            message.setText("輸入/dog 去搵隻狗先");
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
                                        "\uD83D\uDCAA生命: " + dictionary.get(update.getMessage().getFrom().getId()).getHealth() +"/"+dictionary.get(update.getMessage().getFrom().getId()).getMaximumHealth()+ "\n" +
                                        "\uD83E\uDD47等級: "+  dictionary.get(update.getMessage().getFrom().getId()).getLevel() + "\n" +
                                        "\uD83C\uDD99經驗值: "  + dictionary.get(update.getMessage().getFrom().getId()).getExp() + "/" + growCurve.get(dictionary.get(update.getMessage().getFrom().getId()).getLevel()) +"exp\n" +
                                        "\uD83C\uDF54飢餓值: " + dictionary.get(update.getMessage().getFrom().getId()).getHunger() + "/100\n" +
                                        "❤親密度: " + dictionary.get(update.getMessage().getFrom().getId()).getRelationship() + "\n" +
                                        "\uD83D\uDCB0金錢: " + dictionary.get(update.getMessage().getFrom().getId()).getCurrency());
                            } else {
                                message.setText("輸入/name [name] 幫我改過名先啦");
                            }


                        } else {
                            message.setText("輸入/dog 去搵隻狗先");
                        }

                        try {
                            execute(message);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    if (command.equals(("/achievements"))) {
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
                            try {
                                execute(message);
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                    }
                    if (command.equals("/version")){
                        message.setText("小狗的RPG歷險記\uD83D\uDC36\n" +
                                "V1.2.1更新(7/11/2019)\n" +
                                "-修復大量文本錯誤\n" +
                                "-更改遊戲平衡性\n" +
                                "-測試成就系統\n" +
                                "\n" +
                                "\uD83D\uDC36\uD83D\uDC36\uD83D\uDC36\uD83D\uDC36\uD83D\uDC36\uD83D\uDC36\uD83D\uDC36\uD83D\uDC36\uD83D\uDC36\uD83D\uDC36\uD83D\uDC36\uD83D\uDC36\uD83D\uDC36\uD83D\uDC36\n" +
                                "更新預告\uD83D\uDD5B\n" +
                                "\uD83C\uDF1FV1.3更新\n" +
                                "-成就系統\n" +
                                "-競技場系統\n" +
                                "-完善的等級系統\n" +
                                "\n" +
                                "\uD83C\uDF1FV1.4更新\n" +
                                "-新增好友系統\n" +
                                "-新增物品庫\n" +
                                "-新增送禮系統\n" +
                                "\n" +
                                "\uD83D\uDD19\uD83D\uDD19\uD83D\uDD19\uD83D\uDD19\uD83D\uDD19\uD83D\uDD19\uD83D\uDD19\uD83D\uDD19\uD83D\uDD19\uD83D\uDD19\uD83D\uDD19\uD83D\uDD19\uD83D\uDD19\uD83D\uDD19\n" +
                                "舊版本資訊\n" +
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
                                "-可以餵佢食野\n" );
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
                                        "\uD83C\uDF82生日蛋糕$600 飽食度:50 HP:完全恢復") ;

                                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                                List<InlineKeyboardButton> rowInline = new ArrayList<>();
                                List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
                                rowInline.add(new InlineKeyboardButton().setText("牛奶").setCallbackData("food_milk"));
                                rowInline.add(new InlineKeyboardButton().setText("狗糧").setCallbackData("food_dogfood"));
                                rowInline.add(new InlineKeyboardButton().setText("肉").setCallbackData("food_meat"));
                                rowInline1.add(new InlineKeyboardButton().setText("燒雞串").setCallbackData("food_chicken"));
                                rowInline1.add(new InlineKeyboardButton().setText("朱古力").setCallbackData("food_chocolate"));
                                rowInline1.add(new InlineKeyboardButton().setText("波棒糖").setCallbackData("food_lollipop"));
                                rowInline1.add(new InlineKeyboardButton().setText("生日蛋糕").setCallbackData("food_birthdaycake"));
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
                            message.setText("輸入/dog 去搵隻狗先啦");
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
                            message.setText("輸入/dog 去搵隻狗先啦");
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
                                rowInline.add(new InlineKeyboardButton().setText("2. 開發中").setCallbackData("game_two"));
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

                    if (command.equals(("admin setDogData"))){
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
                    if (dictionary.get(update.getMessage().getFrom().getId()).isToggle_settingRelationship()){
                        if (Integer.parseInt(update.getMessage().getText()) >=0 && Integer.parseInt(update.getMessage().getText()) <=100 ){
                            dictionary.get(update.getMessage().getFrom().getId()).setRelationship(Integer.parseInt(update.getMessage().getText()) );
                            dictionary.get(update.getMessage().getFrom().getId()).setToggle_settingRelationship(false);
                        }
                            dictionary.get(update.getMessage().getFrom().getId()).setToggle_settingRelationship(false);
                    }
                    if (dictionary.get(update.getMessage().getFrom().getId()).isToggle_settingCurrency()){
                        if (Integer.parseInt(update.getMessage().getText()) >=0){
                            dictionary.get(update.getMessage().getFrom().getId()).setCurrency(Integer.parseInt(update.getMessage().getText()));
                            dictionary.get(update.getMessage().getFrom().getId()).setToggle_settingCurrency(false);
                        }
                            dictionary.get(update.getMessage().getFrom().getId()).setToggle_settingCurrency(false);

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
                message.setText("\uD83D\uDC3E" + dictionary.get(update.getMessage().getFrom().getId()).getName() + "你隻狗死左啦\uD83D\uDC80");
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
                            }
                            else{
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
                if (!(dictionary.get(update.getCallbackQuery().getFrom().getId()).getHunger() + 30 > 100)) {
                    if (dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() >= 200) {
                        new_message.setText("用$200餵左波板糖\n飢餓值增加左: 30\nHP恢復左25");
                        dictionary.get(update.getCallbackQuery().getFrom().getId()).setHunger(dictionary.get(update.getCallbackQuery().getFrom().getId()).getHunger() + 30);
                        dictionary.get(update.getCallbackQuery().getFrom().getId()).setCurrency(dictionary.get(update.getCallbackQuery().getFrom().getId()).getCurrency() - 200);
                       //check下最大生命值
                        if ((dictionary.get(update.getCallbackQuery().getFrom().getId()).getHealth() + 25) > dictionary.get(update.getCallbackQuery().getFrom().getId()).getMaximumHealth()) {
                            dictionary.get(update.getCallbackQuery().getFrom().getId()).setHealth(dictionary.get(update.getCallbackQuery().getFrom().getId()).getMaximumHealth()); //set最大生命值
                        }
                        else{
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
                if (!(dictionary.get(update.getCallbackQuery().getFrom().getId()).getHunger() + 75 > 100)) {
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
                String result;
                if (call_data.equals("game_one")) {
                    new_message.setText("你想出咩?");
                    InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                    List<InlineKeyboardButton> rowInline = new ArrayList<>();
                    List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
                    rowInline.add(new InlineKeyboardButton().setText("\uD83D\uDD90包").setCallbackData("paper"));
                    rowInline.add(new InlineKeyboardButton().setText("✌剪").setCallbackData("scissors"));
                    rowInline.add(new InlineKeyboardButton().setText("\uD83D\uDC4A揼").setCallbackData("stone"));

                    // Set the keyboard to the markup
                    rowsInline.add(rowInline);
                    // Add it to the message
                    markupInline.setKeyboard(rowsInline);
                    new_message.setReplyMarkup(markupInline);

                }
                if (call_data.equals("paper")) {
                    result = playGame1(1, update);
                    new_message.setText(result);
                }
                if (call_data.equals("scissors")) {
                    result = playGame1(2, update);
                    new_message.setText(result);
                }

                if (call_data.equals("stone")) {
                    result = playGame1(3, update);
                    new_message.setText(result);
                }
                if (call_data.equals("setDogData_relationship")) {
                        new_message.setText("輸入你想設定的數字(0-100)");
                        dictionary.get(update.getCallbackQuery().getFrom().getId()).setToggle_settingRelationship(true);
                }
                if (call_data.equals("setDogData_currency")){
                    new_message.setText(("輸入你想設定的數字(>=0)"));
                    dictionary.get(update.getCallbackQuery().getFrom().getId()).setToggle_settingCurrency(true);
                }

                try {
                    execute(new_message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
               // checkHPleft(update);
               checkAchievements(update);
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
