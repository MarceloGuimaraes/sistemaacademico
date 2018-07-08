package br.com.pucminas.projeto.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.pucminas.projeto.model.Aluno;



public interface Alunos extends JpaRepository<Aluno, Long>{
	
	public List<Aluno> findByNomeContaining(String nome);

}
