package com.upgrad.quora.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Table(name = "answer")
public class AnswerEntity {
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
    private QuestionEntity question;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnswerEntity)) return false;
        AnswerEntity that = (AnswerEntity) o;
        return id == that.id &&
                Objects.equals(uuid, that.uuid) &&
                Objects.equals(user, that.user) &&
                Objects.equals(ans, that.ans) &&
                Objects.equals(date, that.date) &&
                Objects.equals(question, that.question);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uuid, user, ans, date, question);
    }

    @Override
    public String toString() {
        return "AnswerEntity{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", user=" + user +
                ", ans='" + ans + '\'' +
                ", date=" + date +
                ", question=" + question +
                '}';
    }
}
