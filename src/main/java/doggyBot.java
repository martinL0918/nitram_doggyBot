import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;


public class doggyBot extends TelegramLongPollingBot {
    private int relationship = -1;
    private HashMap<Integer,Dog> dictionary= new HashMap<>();
    public void onUpdateReceived(Update update) {
        System.out.println("Received message from "+update.getMessage().getFrom().getFirstName()+": " +update.getMessage().getText());
        if (!dictionary.containsKey(update.getMessage().getFrom().getId())){
            dictionary.put(update.getMessage().getFrom().getId(),new Dog());
        }
        else if (dictionary.containsKey((update.getMessage().getFrom().getId()))){
            relationship = dictionary.get(update.getMessage().getFrom().getId()).getRelationship();
        }
        String command = update.getMessage().getText();
        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId());

        if (command.equals("add")) {
            relationship+= 1;
            dictionary.get(update.getMessage().getFrom().getId()).setRelationship(relationship);
        }
        if (command.equals("print")) {
            message.setText(Integer.toString(relationship));
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        if (command.equals("clear")){
            dictionary.clear();
        }
        if (command.equals("printhash")){
            for (int temp: dictionary.keySet()){
                System.out.println(temp);
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
