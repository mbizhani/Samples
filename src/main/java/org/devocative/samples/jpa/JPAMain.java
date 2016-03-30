package org.devocative.samples.jpa;

import org.devocative.samples.jpa.entity.Book;
import org.devocative.samples.jpa.entity.Student;

import javax.persistence.*;
import java.util.List;

public class JPAMain {
	public static void main(String[] args) {
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test-jpa");
		System.out.println("entityManagerFactory.isOpen() = " + entityManagerFactory.isOpen());

		EntityManager entityManager = entityManagerFactory.createEntityManager();

		saveData(entityManager);

		query(entityManager);


		entityManager.close();
		entityManagerFactory.close();
	}

	private static void query(EntityManager entityManager) {
		Query query = entityManager.createQuery("select s from Student s where s.firstName like ?");
		query.setParameter(1, "Jo%");

		List<Student> students = query.getResultList();
		Student s = students.get(0);
		System.out.println(students.size());
		System.out.println(s.getFirstName());
	}

	private static void saveData(EntityManager entityManager) {
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();

		Student student = new Student();
		student.setFirstName("John");
		student.setLastName("Doe");
		Book book1 = new Book();
		book1.setName("Book 1");
		Book book2 = new Book();
		book2.setName("Book 2");

		student.getBooks().add(book1);
		student.getBooks().add(book2);
		entityManager.persist(student);

		transaction.commit();
	}
}
