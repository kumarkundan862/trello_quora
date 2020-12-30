package com.upgrad.quora.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Table(name = "QUESTION")

@NamedQueries({
        @NamedQuery(name = "questionByUuid", query = "select q from Question q where q.uuid = " +
                ":uuid")
})
public class Question {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "uuid")
    @Size(max = 200)
    @NotNull
    private String uuid;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id")
    @NotNull
    private UserEntity user;

    @Column(name = "content")
    @Size(max = 500)
    @NotNull
    private String content;

    @Column(name = "date")
    @NotNull
    private ZonedDateTime date;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return id == question.id &&
                Objects.equals(uuid, question.uuid) &&
                Objects.equals(user, question.user) &&
                Objects.equals(content, question.content) &&
                Objects.equals(date, question.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uuid, user, content, date);
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", user=" + user +
                ", content='" + content + '\'' +
                ", date=" + date +
                '}';
    }
}
