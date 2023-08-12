package com.example.collector.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(
        entity = Game.class,
        parentColumns = "gameId",
        childColumns = "gameId",
        onDelete = ForeignKey.CASCADE
)
)
public class Item {

    @PrimaryKey
    @ColumnInfo(name= "id")
    private int id;

    @NonNull
    @ColumnInfo(name="gameId")
    private int gameId;

    @NonNull
    @ColumnInfo(name= "imagePath")
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

    @NonNull
    @ColumnInfo(name = "acquired")
    private boolean acquired;

    public Item(int gameId, @NonNull String imagePath, @NonNull String name, @NonNull String description, @NonNull String prerequisites, @NonNull String location) {
        this.gameId = gameId;
        this.imagePath = imagePath;
        this.name = name;
        this.description = description;
        this.prerequisites = prerequisites;
        this.location = location;
        this.acquired = false;
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

    public boolean isAcquired() {
        return acquired;
    }

    public void setAcquired(boolean acquired) {
        this.acquired = acquired;
    }
}
