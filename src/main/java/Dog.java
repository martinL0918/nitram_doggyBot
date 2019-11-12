import java.util.*;

public class Dog {
    private int health;
    private int maximumHealth;
    private int hunger;
    private int generation;
    //private int love;
    private String name;
    //private String loveStatus;
    private int dogCounter;//睇下有無開過狗
    private String sex;
    private boolean confirm;
    private int temp;
    private boolean toggle_number;
    private int relationship;
    private boolean exploring = false;
    private int currency;
    private boolean dogAlive = true;
    private Timer timer;
    private Timer hungerTimer;
    private boolean running;
    private boolean toggle_settingRelationship;
    private boolean toggle_settingCurrency;
    private boolean toggle_settingLevel;
    private int level;
    private int exp;
    private Set<String> achievedAchievements = new HashSet<>();
    private boolean inArena = false;
    private int arenaCounter = 0 ;
    private HashMap<String,Integer> inventory = new HashMap<>();
    private HashMap<String,Integer> giftInventory = new HashMap<>();
    private HashMap<String, Integer> foundFriends= new HashMap<>();
    private String friendsPointer;
    private int arenaWinCounter =0;
    private int relationship_achievement = 0;
    private int command_achievement = 0;
    private int level_achievement = 0;
    private int asset_achievement = 0;
    private int arena_achievement = 0;
    private int friends_achievement =9;



    public Timer getHungerTimer() {
        return hungerTimer;
    }

    public void setHungerTimer(Timer hungerTimer) {
        this.hungerTimer = hungerTimer;
    }

    public Dog() {
        this.health = 30;
        this.maximumHealth = 30;
        this.hunger = 50;
        //this.current = current;
        this.generation = 1;
        //this.love = 0;
        this.name = "";
        //this.loveStatus = loveStatus;
        this.dogCounter = 0;
        this.sex = "你好";
        this.confirm = false;
        this.temp = 0;
        this.toggle_number = false;
        this.relationship = 0;
        this.exploring = false;
        this.currency = 0;
        this.dogAlive = true;
        this.exploring = false;
        this.timer = new Timer();
        this.hungerTimer = new Timer();
        this.running = false;
        this.toggle_settingRelationship = false;
        this.toggle_settingCurrency = false;
        this.level = 1;
        this.exp = 1;
        this.friendsPointer = "";
    }




    public int getCurrency() {
        return currency;
    }

    public void setCurrency(int currency) {
        this.currency = currency;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getHunger() {
        return hunger;
    }

    public void setHunger(int hunger) {
        this.hunger = hunger;
    }



    public int getGeneration() {
        return generation;
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }

   /* public int getLove() {
        return love;
    }*/

    /*public void setLove(int love) {
        this.love = love;
    }*/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*public String getLoveStatus() {
        return loveStatus;
    }*/

    /*public void setLoveStatus(String loveStatus) {
        this.loveStatus = loveStatus;
    }*/

    public int getDogCounter() {
        return dogCounter;
    }

    public void setDogCounter(int dogCounter) {
        this.dogCounter = dogCounter;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public boolean isConfirm() {
        return confirm;
    }

    public void setConfirm(boolean confirm) {
        this.confirm = confirm;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public boolean isToggle_number() {
        return toggle_number;
    }

    public void setToggle_number(boolean toggle_number) {
        this.toggle_number = toggle_number;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public int getRelationship() {
        return relationship;
    }

    public void setRelationship(int relationship) {
        this.relationship = relationship;
    }

    public boolean isExploring() {
        return exploring;
    }

    public void setExploring(boolean exploring) {
        this.exploring = exploring;
    }

    public boolean isDogAlive() {
        return dogAlive;
    }

    public void setDogAlive(boolean dogAlive) {
        this.dogAlive = dogAlive;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isToggle_settingRelationship() {
        return toggle_settingRelationship;
    }

    public void setToggle_settingRelationship(boolean toggle_settingRelationship) {
        this.toggle_settingRelationship = toggle_settingRelationship;
    }

    public boolean isToggle_settingCurrency() {
        return toggle_settingCurrency;
    }

    public void setToggle_settingCurrency(boolean toggle_settingCurrency) {
        this.toggle_settingCurrency = toggle_settingCurrency;
    }

    public boolean isToggle_settingLevel() {
        return toggle_settingLevel;
    }

    public void setToggle_settingLevel(boolean toggle_settingLevel) {
        this.toggle_settingLevel = toggle_settingLevel;
    }

    public int getMaximumHealth() {
        return maximumHealth;
    }

    public void setMaximumHealth(int maxmimumHealth) {
        this.maximumHealth = maxmimumHealth;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public Set<String> getAchievedAchievements() {
        return achievedAchievements;
    }

    public void setAchievedAchievements(Set<String> achievedAchievements) {
        this.achievedAchievements = achievedAchievements;
    }

    public boolean isInArena() {
        return inArena;
    }

    public void setInArena(boolean inArena) {
        this.inArena = inArena;
    }

    public int getArenaCounter() {
        return arenaCounter;
    }

    public void setArenaCounter(int arenaCounter) {
        this.arenaCounter = arenaCounter;
    }

    public HashMap<String, Integer> getInventory() {
        return inventory;
    }

    public void setInventory(HashMap<String, Integer> inventory) {
        this.inventory = inventory;
    }

    public HashMap<String, Integer> getFoundFriends() {
        return foundFriends;
    }

    public void setFoundFriends(HashMap<String, Integer> foundFriends) {
        this.foundFriends = foundFriends;
    }

    public HashMap<String, Integer> getGiftInventory() {
        return giftInventory;
    }

    public void setGiftInventory(HashMap<String, Integer> giftInventory) {
        this.giftInventory = giftInventory;
    }

    public String getFriendsPointer() {
        return friendsPointer;
    }

    public void setFriendsPointer(String friendsPointer) {
        this.friendsPointer = friendsPointer;
    }

    public int getArenaWinCounter() {
        return arenaWinCounter;
    }

    public void setArenaWinCounter(int arenaWinCounter) {
        this.arenaWinCounter = arenaWinCounter;
    }

    public int getRelationship_achievement() {
        return relationship_achievement;
    }

    public void setRelationship_achievement(int relationship_achievement) {
        this.relationship_achievement = relationship_achievement;
    }

    public int getCommand_achievement() {
        return command_achievement;
    }

    public void setCommand_achievement(int command_achievement) {
        this.command_achievement = command_achievement;
    }

    public int getLevel_achievement() {
        return level_achievement;
    }

    public void setLevel_achievement(int level_achievement) {
        this.level_achievement = level_achievement;
    }

    public int getAsset_achievement() {
        return asset_achievement;
    }

    public void setAsset_achievement(int asset_achievement) {
        this.asset_achievement = asset_achievement;
    }

    public int getArena_achievement() {
        return arena_achievement;
    }

    public void setArena_achievement(int arena_achievement) {
        this.arena_achievement = arena_achievement;
    }

    public int getFriends_achievement() {
        return friends_achievement;
    }

    public void setFriends_achievement(int friends_achievement) {
        this.friends_achievement = friends_achievement;
    }
}
