package de.pme.collector.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;


// class to hold item information
@Entity(foreignKeys = @ForeignKey(
        entity = Game.class,
        parentColumns = "id",
        childColumns = "gameId",
        onDelete = ForeignKey.CASCADE),
        indices = { @Index("gameId") }
)
public class Item {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "gameId")
    private int gameId;

    @NonNull
    @ColumnInfo(name = "imagePath")
    private String imagePath;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @NonNull
    @ColumnInfo(name = "description")
    private String description;

    @NonNull
    @ColumnInfo(name = "prerequisites")
    private String prerequisites;

    @NonNull
    @ColumnInfo(name = "location")
    private String location;

    @ColumnInfo(name = "acquired")
    private boolean obtained;


    public Item(int gameId, @NonNull String imagePath, @NonNull String name, @NonNull String description, @NonNull String prerequisites, @NonNull String location) {
        this.gameId = gameId;
        this.imagePath = imagePath;
        this.name = name;
        this.description = description;
        this.prerequisites = prerequisites;
        this.location = location;
        this.obtained = false;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getGameId() {
        return gameId;
    }


    @NonNull
    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(@NonNull String imagePath) {
        this.imagePath = imagePath;
    }


    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }


    @NonNull
    public String getDescription() {
        return description;
    }

    public void setDescription(@NonNull String description) {
        this.description = description;
    }


    @NonNull
    public String getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(@NonNull String prerequisites) {
        this.prerequisites = prerequisites;
    }


    @NonNull
    public String getLocation() {
        return location;
    }

    public void setLocation(@NonNull String location) {
        this.location = location;
    }


    public boolean isObtained() {
        return obtained;
    }

    public void setObtained(boolean obtained) {
        this.obtained = obtained;
    }
}