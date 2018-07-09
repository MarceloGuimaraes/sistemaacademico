package br.com.pucminas.projeto.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.pucminas.projeto.model.Curso;



public interface Cursos extends JpaRepository<Curso, Long>{
	
	public List<Curso> findByNomeContaining(String nome);

}
