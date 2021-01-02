package com.upgrad.quora.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Table(name = "answer")
@NamedQueries({
        @NamedQuery(name = "getAnswerByUuid", query = "select a from AnswerEntity a where a.uuid " +
                "= :uuid"),
        @NamedQuery(name = "answersByQid",query = "select a from AnswerEntity a where a.question.uuid" +
                " = :qid"),
        @NamedQuery(name = "deleteAnswer", query = "delete from AnswerEntity q where q.uuid = :uuid")
})
public class AnswerEntity implements Serializable {

    public AnswerEntity() {}


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
    private String answer;

    @ManyToOne
    @JoinColumn(name = "question_id")
    @NotNull
    private QuestionEntity question;

    @Column(name = "date")
    @NotNull
    private ZonedDateTime date;

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

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String ans) {
        this.answer = ans;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public QuestionEntity getQuestion() {
        return question;
    }

    public void setQuestion(QuestionEntity question) {
        this.question = question;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "AnswerEntity{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", user=" + user +
                ", answer='" + answer + '\'' +
                ", question=" + question +
                ", date=" + date +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnswerEntity)) return false;
        AnswerEntity that = (AnswerEntity) o;
        return getId() == that.getId() &&
                Objects.equals(getUuid(), that.getUuid()) &&
                Objects.equals(getUser(), that.getUser()) &&
                Objects.equals(getAnswer(), that.getAnswer()) &&
                Objects.equals(getQuestion(), that.getQuestion()) &&
                Objects.equals(getDate(), that.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUuid(), getUser(), getAnswer(), getQuestion(), getDate());
    }
}
