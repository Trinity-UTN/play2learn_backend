package trinity.play2learn.backend.benefits;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.year.models.Year;
import trinity.play2learn.backend.benefits.dtos.benefit.BenefitRequestDto;
import trinity.play2learn.backend.benefits.dtos.benefit.BenefitResponseDto;
import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitPurchaseRequestDto;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.models.BenefitCategory;
import trinity.play2learn.backend.benefits.models.BenefitColor;
import trinity.play2learn.backend.benefits.models.BenefitIcon;
import trinity.play2learn.backend.benefits.models.BenefitPurchase;
import trinity.play2learn.backend.benefits.models.BenefitPurchaseState;
import trinity.play2learn.backend.benefits.models.BenefitState;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.profile.profile.models.Profile;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BenefitTestMother {

    // Constantes de configuración
    private static final String DEFAULT_BENEFIT_NAME = "15 minutos más de recreo";
    private static final String DEFAULT_DESCRIPTION = "Un recreo extra de 15 minutos";
    private static final Long DEFAULT_COST = 100L;
    private static final Integer DEFAULT_PURCHASE_LIMIT = 50;
    private static final Integer DEFAULT_PURCHASE_LIMIT_PER_STUDENT = 1;

    // Constantes de prueba comunes
    public static final Long DEFAULT_BENEFIT_ID = 1001L;
    public static final Long DEFAULT_SUBJECT_ID = 201L;
    public static final Long DEFAULT_TEACHER_ID = 301L;
    public static final Long DEFAULT_STUDENT_ID = 401L;
    public static final Long DEFAULT_COURSE_ID = 101L;
    public static final Long DEFAULT_PURCHASE_ID = 501L;

    public static final String DEFAULT_TEACHER_EMAIL = "teacher@example.com";
    public static final String DEFAULT_UNAUTHORIZED_TEACHER_EMAIL = "other.teacher@example.com";
    public static final String DEFAULT_STUDENT_EMAIL = "student@example.com";

    // Builders para BenefitRequestDto
    public static BenefitRequestDto.BenefitRequestDtoBuilder benefitRequestBuilder(Long subjectId) {
        return BenefitRequestDto.builder()
            .name(DEFAULT_BENEFIT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .cost(DEFAULT_COST)
            .purchaseLimit(DEFAULT_PURCHASE_LIMIT)
            .purchaseLimitPerStudent(DEFAULT_PURCHASE_LIMIT_PER_STUDENT)
            .endAt(LocalDateTime.now().plusDays(30))
            .subjectId(subjectId)
            .icon(BenefitIcon.SKIP)
            .category(BenefitCategory.EXTRAS)
            .color(BenefitColor.RED);
    }

    // Builders para Benefit
    public static Benefit.BenefitBuilder benefitBuilder(Long id, Subject subject) {
        return Benefit.builder()
            .id(id)
            .name(DEFAULT_BENEFIT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .cost(DEFAULT_COST)
            .purchaseLimit(DEFAULT_PURCHASE_LIMIT)
            .purchasesLeft(DEFAULT_PURCHASE_LIMIT)
            .purchaseLimitPerStudent(DEFAULT_PURCHASE_LIMIT_PER_STUDENT)
            .endAt(LocalDateTime.now().plusDays(30))
            .subject(subject)
            .icon(BenefitIcon.SKIP)
            .category(BenefitCategory.EXTRAS)
            .color(BenefitColor.RED);
    }

    public static Benefit benefit(Long id, Subject subject) {
        return benefitBuilder(id, subject).build();
    }

    public static Benefit benefitWithTeacher(Long id, Subject subject, String teacherEmail) {
        Benefit benefit = benefit(id, subject);
        if (subject.getTeacher() != null && subject.getTeacher().getUser() != null) {
            subject.getTeacher().getUser().setEmail(teacherEmail);
        }
        return benefit;
    }

    public static Benefit expiredBenefit(Long id, Subject subject) {
        return benefitBuilder(id, subject)
            .endAt(LocalDateTime.now().minusDays(1))
            .build();
    }

    public static Benefit unlimitedBenefit(Long id, Subject subject) {
        return benefitBuilder(id, subject)
            .purchaseLimit(null)
            .purchasesLeft(null)
            .purchaseLimitPerStudent(null)
            .build();
    }

    public static Benefit deletedBenefit(Long id, Subject subject) {
        Benefit benefit = benefit(id, subject);
        benefit.delete();
        return benefit;
    }

    public static Benefit benefitWithPurchasesLeft(Long id, Subject subject, Integer purchasesLeft) {
        return benefitBuilder(id, subject)
            .purchasesLeft(purchasesLeft)
            .build();
    }

    // Builders para BenefitPurchase
    public static BenefitPurchase.BenefitPurchaseBuilder benefitPurchaseBuilder(Benefit benefit, Student student) {
        return BenefitPurchase.builder()
            .id(501L)
            .benefit(benefit)
            .student(student)
            .state(BenefitPurchaseState.PURCHASED)
            .purchasedAt(LocalDateTime.now())
            .usedAt(null);
    }

    public static BenefitPurchase benefitPurchase(Benefit benefit, Student student) {
        return benefitPurchaseBuilder(benefit, student).build();
    }

    public static BenefitPurchase purchasedBenefitPurchase(Benefit benefit, Student student) {
        return benefitPurchaseBuilder(benefit, student)
            .state(BenefitPurchaseState.PURCHASED)
            .build();
    }

    public static BenefitPurchase useRequestedBenefitPurchase(Benefit benefit, Student student) {
        return benefitPurchaseBuilder(benefit, student)
            .state(BenefitPurchaseState.USE_REQUESTED)
            .build();
    }

    public static BenefitPurchase usedBenefitPurchase(Benefit benefit, Student student) {
        return benefitPurchaseBuilder(benefit, student)
            .state(BenefitPurchaseState.USED)
            .usedAt(LocalDateTime.now())
            .build();
    }

    public static BenefitPurchase deletedBenefitPurchase(Benefit benefit, Student student) {
        BenefitPurchase purchase = benefitPurchase(benefit, student);
        purchase.setDeletedAt(LocalDateTime.now());
        return purchase;
    }

    // Builders para BenefitRequestDto
    public static BenefitPurchaseRequestDto.BenefitPurchaseRequestDtoBuilder benefitPurchaseRequestBuilder(Long benefitId) {
        return BenefitPurchaseRequestDto.builder()
            .benefitId(benefitId);
    }

    public static BenefitPurchaseRequestDto benefitPurchaseRequest(Long benefitId) {
        return benefitPurchaseRequestBuilder(benefitId).build();
    }

    // Builders para Subject
    public static Subject subjectWithTeacher(Long id, String name, Course course, Teacher teacher) {
        return Subject.builder()
            .id(id)
            .name(name)
            .course(course)
            .teacher(teacher)
            .optional(false)
            .actualBalance(0.0)
            .initialBalance(0.0)
            .students(new ArrayList<>())
            .build();
    }

    public static Subject subjectWithTeacher(Long id, Course course, Teacher teacher) {
        return subjectWithTeacher(id, "Matemática", course, teacher);
    }

    public static Subject subjectWithTeacherAndStudent(Long id, Course course, Teacher teacher, Student student) {
        Subject subject = subjectWithTeacher(id, course, teacher);
        subject.setStudents(new ArrayList<>(List.of(student)));
        return subject;
    }

    public static Subject subjectWithTeacherAndStudent(Long subjectId, Long courseId, String teacherEmail, Long studentId, String studentEmail) {
        Course course = course(courseId);
        Teacher teacher = teacher(DEFAULT_TEACHER_ID, teacherEmail);
        Student student = student(studentId, studentEmail);
        return subjectWithTeacherAndStudent(subjectId, course, teacher, student);
    }

    public static Subject defaultSubjectWithTeacherAndStudent() {
        return subjectWithTeacherAndStudent(
            DEFAULT_SUBJECT_ID,
            DEFAULT_COURSE_ID,
            DEFAULT_TEACHER_EMAIL,
            DEFAULT_STUDENT_ID,
            DEFAULT_STUDENT_EMAIL
        );
    }

    // Builders para Teacher
    public static Teacher teacher(Long id, String dni, String email) {
        User user = teacherUser(300L + id, email);
        return Teacher.builder()
            .id(id)
            .name("Juan")
            .lastname("Pérez")
            .dni(dni)
            .user(user)
            .build();
    }

    public static Teacher teacher(Long id, String email) {
        return teacher(id, "12345678", email);
    }

    public static User teacherUser(Long id, String email) {
        return User.builder()
            .id(id)
            .email(email)
            .password("hashed")
            .role(Role.ROLE_TEACHER)
            .build();
    }

    // Builders para Student
    public static Student student(Long id, String dni, Long courseId, String email) {
        User user = studentUser(700L + id, email);
        Course course = course(courseId);
        Student student = Student.builder()
            .id(id)
            .name("Ana")
            .lastname("Gómez")
            .dni(dni)
            .course(course)
            .user(user)
            .build();
        Profile profile = Profile.builder()
            .id(900L + id)
            .student(student)
            .build();
        Wallet wallet = Wallet.builder()
            .id(800L + id)
            .student(student)
            .balance(1000.0)
            .invertedBalance(0.0)
            .build();
        student.setProfile(profile);
        student.setWallet(wallet);
        return student;
    }

    public static Student student(Long id, String email) {
        return student(id, "87654321", 101L, email);
    }

    public static Student studentWithWalletBalance(Long id, String email, Double balance) {
        Student student = student(id, email);
        student.getWallet().setBalance(balance);
        return student;
    }

    public static User studentUser(Long id, String email) {
        return User.builder()
            .id(id)
            .email(email)
            .password("hashed")
            .role(Role.ROLE_STUDENT)
            .build();
    }

    public static Course course(Long id) {
        return Course.builder()
            .id(id)
            .name("3ro A")
            .year(Year.builder()
                .id(2025L)
                .name("2025")
                .build())
            .build();
    }

    // Builders para BenefitResponseDto
    public static BenefitResponseDto.BenefitResponseDtoBuilder benefitResponseBuilder(Long id) {
        return BenefitResponseDto.builder()
            .id(id)
            .name(DEFAULT_BENEFIT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .cost(DEFAULT_COST)
            .purchaseLimit(DEFAULT_PURCHASE_LIMIT)
            .purchaseLimitPerStudent(DEFAULT_PURCHASE_LIMIT_PER_STUDENT)
            .endAt(LocalDateTime.now().plusDays(30))
            .state(BenefitState.PUBLISHED)
            .icon(BenefitIcon.SKIP)
            .category(BenefitCategory.EXTRAS)
            .color(BenefitColor.RED);
    }

    // Helpers para User
    public static User user(String email, Role role) {
        return User.builder()
            .id(role == Role.ROLE_TEACHER ? 300L : 700L)
            .email(email)
            .password("hashed")
            .role(role)
            .build();
    }

    public static User teacherUser(String email) {
        return user(email, Role.ROLE_TEACHER);
    }

    public static User studentUser(String email) {
        return user(email, Role.ROLE_STUDENT);
    }

    // Helpers de defaults para simplificar tests
    public static Subject defaultSubject() {
        return subjectWithTeacher(
            DEFAULT_SUBJECT_ID,
            course(DEFAULT_COURSE_ID),
            teacher(DEFAULT_TEACHER_ID, DEFAULT_TEACHER_EMAIL)
        );
    }

    public static Benefit defaultBenefit() {
        return benefit(DEFAULT_BENEFIT_ID, defaultSubject());
    }

    public static Student defaultStudent() {
        return student(DEFAULT_STUDENT_ID, DEFAULT_STUDENT_EMAIL);
    }

    public static Teacher defaultTeacher() {
        return teacher(DEFAULT_TEACHER_ID, DEFAULT_TEACHER_EMAIL);
    }
}

