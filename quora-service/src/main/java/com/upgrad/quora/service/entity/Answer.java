package com.upgrad.quora.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Table(name = "answer")
public class Answer {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "uuid")
    @Size(max = 200)
    @NotNull
    private String uuid;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private UserEntity user;

    @Column(name = "ans")
    @Size(max = 255)
    @NotNull
    private String ans;

    @Column(name = "date")
    @NotNull
    private ZonedDateTime date;

    @ManyToOne
    @JoinColumn(name = "question_id")
    @NotNull
    private Question question;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Answer answer = (Answer) o;
        return id == answer.id &&
                Objects.equals(uuid, answer.uuid) &&
                Objects.equals(user, answer.user) &&
                Objects.equals(ans, answer.ans) &&
                Objects.equals(date, answer.date) &&
                Objects.equals(question, answer.question);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uuid, user, ans, date, question);
    }

    @Override
    public String toString() {
        return "Answer{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", user=" + user +
                ", ans='" + ans + '\'' +
                ", date=" + date +
                ", question=" + question +
                '}';
    }
}
