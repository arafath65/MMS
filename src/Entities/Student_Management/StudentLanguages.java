
package Entities.Student_Management;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "student_languages")
public class StudentLanguages implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_languages_id")
    private Integer studentLanguagesId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @Column(name = "english")
    private Boolean english = false;

    @Column(name = "sinhala")
    private Boolean sinhala = false;

    @Column(name = "tamil")
    private Boolean tamil = false;

    @Column(name = "arabic")
    private Boolean arabic = false;

    public Integer getStudentLanguagesId() {
        return studentLanguagesId;
    }

    public void setStudentLanguagesId(Integer studentLanguagesId) {
        this.studentLanguagesId = studentLanguagesId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Boolean getEnglish() {
        return english;
    }

    public void setEnglish(Boolean english) {
        this.english = english;
    }

    public Boolean getSinhala() {
        return sinhala;
    }

    public void setSinhala(Boolean sinhala) {
        this.sinhala = sinhala;
    }

    public Boolean getTamil() {
        return tamil;
    }

    public void setTamil(Boolean tamil) {
        this.tamil = tamil;
    }

    public Boolean getArabic() {
        return arabic;
    }

    public void setArabic(Boolean arabic) {
        this.arabic = arabic;
    }
}
