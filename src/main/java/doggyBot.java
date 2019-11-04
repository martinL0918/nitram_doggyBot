import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.vdurmont.emoji.EmojiParser;

import java.util.*;

import static java.lang.Math.toIntExact;


public class doggyBot extends TelegramLongPollingBot {
    private HashMap<Integer, Dog> dictionary = new HashMap<>();
    private int health;
    private int hunger;
    // private int current;
    private int generation;
    //private int love;
    private String name;
    //private String loveStatus;
    private int dogCounter;//睇下有無開過狗
    private String sex;
    private boolean confirm;
    private int temp;
    private boolean toggle_number;
    public Timer timer = new Timer();
    private int relationship;
    private int userID;
    private boolean exploring = false;
    private int currency;


    public void loadUserData(Update update) {
        int user = update.getMessage().getFrom().getId();
        health = dictionary.get(user).getHealth();
        hunger = dictionary.get(user).getHunger();
        generation = dictionary.get(user).getGeneration();
        name = dictionary.get(user).getName();
        dogCounter = dictionary.get(user).getDogCounter();
        sex = dictionary.get(user).getSex();
        confirm = dictionary.get(user).isConfirm();
        temp = dictionary.get(user).getTemp();
        toggle_number = dictionary.get(user).isToggle_number();
        relationship = dictionary.get(user).getRelationship();
        currency = dictionary.get(user).getCurrency();
        exploring = dictionary.get(user).isExploring();

    }

    public void removeAllData() {
        health = 100;
        hunger = 50;
        //this.current = current;
        generation = 1;
        //this.love = 0;
        name = "";
        //this.loveStatus = loveStatus;
        dogCounter = 0;
        sex = "";
        confirm = false;
        temp = 0;
        toggle_number = false;
        timer = timer;
        relationship = 0;
    }
    public void setTimer(int seconds,Update update) {

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println(exploring);
                exploring =false;
                dictionary.get(userID).setExploring(false);

                String explore_msg ="test";
                explore_msg = exploreTheWorld();
                SendMessage message = new SendMessage();
                message.setChatId(update.getMessage().getChatId());
                message.setText((name+ "翻左屋介\uD83C\uDFE0啦\n佢頭先"+explore_msg));
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();

                }
            }
        }, seconds * 1000); //?60?-1???
    }
    //睇下隻狗有無名
    public boolean checkHaveName() {
        if (name.equals("") || name.equals("null"))
            return false;
        else
            return true;
    }

    //設定狗的性別
    public void setGender() {
        int x = (int) (Math.random() * 2 + 1);
        if (x == 1)
            dictionary.get(userID).setSex("雄性");
        else
            dictionary.get(userID).setSex("雌性");

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
                dictionary.get(userID).setRelationship(relationship + 1);
            }
            if (input == 3) {//玩家出揼
                result = "你出\uD83D\uDC4A 我出\uD83D\uDD90\n" + "你輸左❌" + "我同你變得更親近";
                dictionary.get(userID).setRelationship(relationship + 2);
            }
        } else if (computer == 1) { //電腦出剪
            if (input == 2) {//玩家出剪
                result = "你出✌ 我出✌\n" + "大家打和\n" + "我對你產生左少少好感\n";
                dictionary.get(userID).setRelationship(relationship + 1);
            }
            if (input == 1) {//玩家出包
                result = "你出\uD83D\uDD90 我出✌\n" + "你輸左❌\n" + "我同你變得更親近\n";
                dictionary.get(userID).setRelationship(relationship + 2);
            }
            if (input == 3) {//玩家出揼
                result = "你出\uD83D\uDC4A 我出✌\n" + "你贏左\uD83C\uDF8A\uD83C\uDF8A\n" + "但親密度好似無咩變化\n";
            }
        } else if (computer == 2) { //電腦出揼
            if (input == 2) {//玩家出剪
                result = "你出✌ 我出\uD83D\uDC4A\n" + "你輸左\n❌" + "我同你變得更親近\n";
                dictionary.get(userID).setRelationship(relationship + 2);
            }
            if (input == 1) {//玩家出包
                result = "你出\uD83D\uDD90 我出\uD83D\uDC4A\n" + "你贏左\uD83C\uDF8A\uD83C\uDF8A\n" + "但親密度好似無咩變化\n";
            }
            if (input == 3) {//玩家出揼
                result = "你出\uD83D\uDC4A 我出\uD83D\uDC4A\n" + "大家打和\n" + "我對你產生左少少好感";
                dictionary.get(userID).setRelationship(relationship + 1);
            }
        }
        return result;
    }

    public String exploreTheWorld(){
        String result ="";

        int randomStatement = (int) (Math.random() * (6-0+1)+0);
        if (randomStatement == 0){
            int cash = (int) (Math.random() * (50-1+1)+1);
            result += "搵到一個寶箱\uD83C\uDF81\n";
            result += "打開後有$" +cash;
            dictionary.get(userID).setCurrency(dictionary.get(userID).getCurrency()+cash);
        }
        if (randomStatement == 1){
            result += "發現一個新地點\uD83D\uDDFB\n";
        }
        if (randomStatement == 2){
            int cash = (int) (Math.random() * (50-1+1)+1);
            result += "係地下執到錢\uD83D\uDCB0\n";
            result+= "有$"+ cash;
            dictionary.get(userID).setCurrency(dictionary.get(userID).getCurrency()+cash);
        }
        if (randomStatement == 3){
            int cash = (int) (Math.random() * (80-30+1)+30);
            int win = (int) (Math.random() * (1-0+1)+0);
            int loss = (int) (Math.random() * (12-2+1)+2);
            result += "遇到HTML怪物\uD83D\uDC7E\n";
            if (win==0){
                result+="\n佢成功打敗HTML怪物\n你賺左$"+cash;
                dictionary.get(userID).setCurrency(dictionary.get(userID).getCurrency()+cash);
            }
            if (win == 1) {
                result += "\n佢打輸左比HTML怪物\uD83D\uDC7E\n生命\uD83D\uDC94減左"+loss;
                dictionary.get(userID).setHealth(dictionary.get(userID).getHealth()-loss);
            }
        }
        if (randomStatement == 4){
            int cash = (int) (Math.random() * (300-100+1)+100);
            result += "發現左Insight的徽章\uD83E\uDD47\n";
            result +="賣左之後有$"+cash;
            dictionary.get(userID).setCurrency(dictionary.get(userID).getCurrency()+cash);
        }
        if (randomStatement == 5){
            result += "識到一個新朋友\uD83C\uDF8E\n";
            result +="心靈❤️富足比金錢\uD83D\uDCB5重要";//有一個心
        }
        if (randomStatement == 6){
            result += "佢頭先出門口撞到腳\n狀態唔好\uD83D\uDE2D探險失敗❌";//有一個cross;
        }
        return result;
    }

    public void onUpdateReceived(Update update) {
        if(exploring == false) {
            if (update.hasMessage() && update.getMessage().hasText()) {
                System.out.println("Received message from " + update.getMessage().getFrom().getFirstName() + ": " + update.getMessage().getText());
                //睇下dictionary原本有無呢個人
                if (!dictionary.containsKey(update.getMessage().getFrom().getId())) { //如果無
                    dictionary.put(update.getMessage().getFrom().getId(), new Dog());
                    userID = update.getMessage().getFrom().getId();

                } else if (dictionary.containsKey((update.getMessage().getFrom().getId()))) {//如果有
                    loadUserData(update);
                    userID = update.getMessage().getFrom().getId();
                }
                String command = update.getMessage().getText();
                SendMessage message = new SendMessage();
                message.setChatId(update.getMessage().getChatId());

                //當玩家輸入/dog
                if (command.equals("/dog")) {
                    if (dogCounter < 1) {
                        setGender();
                        message.setText("野生的" + dictionary.get(userID).getSex() + "狗\uD83D\uDC15出現了\n輸入/name [名字]  去幫隻狗改名:\n ( e.g./name 阿旺)");
                        dictionary.get(userID).setDogCounter(1);
                    } else {
                        message.setText("你已經有" + name + "我了\uD83D\uDC36 (汪!!!!))");
                    }
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
                if (command.equals("/start")) {
                    message.setText("輸入/dog去搵一隻狗");
                }
                //當玩家輸入/name
                if (command.contains("/name")) {
                    String temp = getSpace(update.getMessage().getText());
                    if (dogCounter > 0) {
                        if (!checkHaveName()) {
                            if (!temp.equals("")) {
                                dictionary.get(userID).setName(temp);
                                message.setText("\uD83D\uDC36我叫做" + dictionary.get(userID).getName());
                            } else
                                message.setText("錯啦,識唔識打指令");
                        } else
                            message.setText("\uD83D\uDC36我叫做" + dictionary.get(userID).getName());
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
                    relationship += 1;
                    dictionary.get(update.getMessage().getFrom().getId()).setRelationship(relationship);
                }
                //當玩家輸入/info
                if (command.equals("/info")) {
                    if (dogCounter > 0) {
                        if (checkHaveName()) {
                            message.setText("\uD83D\uDC15狗的資訊\n" +
                                    "\uD83D\uDC36名字: " + name + "\n" +
                                    "♂️♀️性別: " + sex + "\n" +
                                    "\uD83D\uDCAA生命: " + health + "\n" +
                                    "\uD83C\uDF54飢餓值: " + hunger + "\n" +
                                    "❤親密度: " + relationship + "\n"+
                                    "\uD83D\uDCB0金錢: "+ currency);
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
                if (command.equals("/feed")) {
                    if (dogCounter > 0) {
                        if (checkHaveName()) {
                            String temp = getSpace(update.getMessage().getText());
                            message.setText("揀下想餵我食咩");
                            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                            List<InlineKeyboardButton> rowInline = new ArrayList<>();
                            List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
                            rowInline.add(new InlineKeyboardButton().setText("1.狗糧").setCallbackData("food_dogfood"));
                            rowInline.add(new InlineKeyboardButton().setText("2.肉").setCallbackData("food_meat"));
                            rowInline1.add(new InlineKeyboardButton().setText("3.牛奶").setCallbackData("food_milk"));
                            rowInline1.add(new InlineKeyboardButton().setText("4.朱古力").setCallbackData("food_chocolate"));
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
                    if (dogCounter > 0) {
                        message.setText("你係咪真係要殺我 QAQ?");
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
                if (command.equals("/play")) {
                    String temp = getSpace(update.getMessage().getText());
                    if (dogCounter > 0) {
                        if (checkHaveName()) {
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
                            command.equals("輸入/name [name] 幫我改過名先啦");
                    } else
                        command.equals("輸入 /dog 去搵隻狗先啦");
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
                //當玩家輸入/explore
                if (command.equals("/explore")) {
                    String explore_msg="";
                    int[] seconds = {5, 15, 30};
                    int random = (int) (Math.random() * (2 - 0 + 1) + 0);
                    System.out.println("random: " + random);
                    if (dogCounter > 0) {
                        if (checkHaveName()) {
                            message.setText("\uD83D\uDC3E"+ name + "去左探索世界\uD83D\uDDFC"+seconds[random]+"秒");
                            exploring = true;
                            dictionary.get(userID).setExploring(true);
                            setTimer(seconds[random],update);
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
                         }else {
                        message.setText("輸入 /dog 去搵隻狗先啦");
                        try {
                            execute(message);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }


                }
                if (command.equals("admin clear")) {
                    dictionary.clear();
                }
                if (command.equals("admin printhash")) {
                    for (int temp : dictionary.keySet()) {
                        System.out.println(temp);
                    }
                }

            } else if (update.hasCallbackQuery()) {
                // Set variables
                String call_data = update.getCallbackQuery().getData();
                long message_id = update.getCallbackQuery().getMessage().getMessageId();
                long chat_id = update.getCallbackQuery().getMessage().getChatId();
                EditMessageText new_message = new EditMessageText()
                        .setChatId(chat_id)
                        .setMessageId(toIntExact(message_id));
                if (call_data.equals("food_dogfood")) {
                    if (!(dictionary.get(userID).getHunger() + 2 > 100)) {
                        new_message.setText("餵左狗糧\n 飢餓值增加左: 2");
                        dictionary.get(userID).setHunger(dictionary.get(userID).getHunger() + 2);
                    } else {
                        new_message.setText("\uD83D\uDC3E"+name + "太飽了，遲D再餵佢食野啦");
                    }
                }
                if (call_data.equals("food_meat")) {
                    if (!(dictionary.get(userID).getHunger() + 8 > 100)) {
                        new_message.setText("餵左肉\n 飢餓值增加左: 8");
                        dictionary.get(userID).setHunger(dictionary.get(userID).getHunger() + 8);
                    } else {
                        new_message.setText("\uD83D\uDC3E"+name + "太飽了，遲D再餵佢食野啦");
                    }
                }
                if (call_data.equals("food_milk")) {
                    if (!(dictionary.get(userID).getHunger() + 3 > 100)) {
                        new_message.setText("餵左牛奶\n 飢餓值增加左: 3");
                        dictionary.get(userID).setHunger(dictionary.get(userID).getHunger() + 3);
                    } else {
                        new_message.setText("\uD83D\uDC3E"+name + "太飽了，遲D再餵佢食野啦");
                    }
                }

                if (call_data.equals("food_chocolate")) {
                    if (!(dictionary.get(userID).getHunger() + 30 > 100)) {
                        new_message.setText("餵左朱古力\n 飢餓值增加左: 30");
                        dictionary.get(userID).setHunger(dictionary.get(userID).getHunger() + 30);
                    } else {
                        new_message.setText("\uD83D\uDC3E"+name + "會飽到死嫁");
                    }
                }
                //如果玩家決定殺貓
                if (call_data.equals("kill_yes")) {
                    new_message.setText("\uD83D\uDC3E"+name + "已經前往西天了\uD83D\uDC80 R.I.P.");
                    dictionary.remove(userID);
                    removeAllData();
                    for (int temp : dictionary.keySet()) {
                        System.out.println(temp);
                    }
                    System.out.println("Print Complete");
                }
                //如果玩家唔殺貓
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
                    rowInline.add(new InlineKeyboardButton().setText("1\uD83D\uDD90包").setCallbackData("paper"));
                    rowInline.add(new InlineKeyboardButton().setText("2✌剪").setCallbackData("scissors"));
                    rowInline.add(new InlineKeyboardButton().setText("3\uD83D\uDC4A揼").setCallbackData("stone"));

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
                try {
                    execute(new_message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }else{
            String command = update.getMessage().getText();
            SendMessage message = new SendMessage();
            message.setChatId(update.getMessage().getChatId());
            message.setText("\uD83D\uDC3E"+name+"係度探索緊世界\uD83D\uDDFC轉頭再搵佢啦");
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }


    }

    public String getBotUsername() {
        // TODO
        return "doggyBot";
    }

    @Override
    public String getBotToken() {
        // TODO
        return "844161272:AAFSfCUKlWHCukJhjAjzjDze577oYcvdj3k";
    }
}
