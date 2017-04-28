package app.com.xmlparser;

// Pojo class for pizza.xml
class Item {

    private int id, cost;
    private String name, description;

    int getCost() {
        return cost;
    }

    void setCost(int cost) {
        this.cost = cost;
    }

    int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }
}
