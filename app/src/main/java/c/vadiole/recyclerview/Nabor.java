package c.vadiole.recyclerview;

public class Nabor {
    Nabor(String myName,int myPrice, int myPerson) {
        this.name = myName;
        this.person = myPerson;
        this.price = myPrice;
    }
    private String name;
    private int price;
    private int person;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPerson() {
        return person;
    }

    public void setPerson(int person) {
        this.person = person;
    }
}
