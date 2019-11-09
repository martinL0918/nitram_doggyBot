import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;

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
}
