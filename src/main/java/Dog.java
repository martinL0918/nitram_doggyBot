import java.util.Timer;

public class Dog {
    private int health;
    private int hunger;
    //private int current;
    private int generation;
    //private int love;
    private String name;
    //private String loveStatus;
    private int dogCounter;//睇下有無開過狗
    private String sex;
    private boolean confirm ;
    private int temp;
    private boolean toggle_number;
    public Timer timer = new Timer();
    private int relationship;
    private boolean exploring = false;
    private int currency;

    public Dog() {
        this.health = 100;
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
        this.timer = timer;
        this.relationship = 0;
        this.exploring = false;
        this.currency = 0;
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

    /*public int getCurrent() {
        return current;
    }*/

    /*public void setCurrent(int current) {
        this.current = current;
    }*/

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
}
