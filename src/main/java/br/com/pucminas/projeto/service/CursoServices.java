package br.com.pucminas.projeto.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.pucminas.projeto.model.Curso;
import br.com.pucminas.projeto.repository.Cursos;
import br.com.pucminas.projeto.repository.filter.CursoFilter;

@Service
public class CursoServices {
	
	@Autowired
	private Cursos cursos;
	
	public List<Curso> findAll() {
		return cursos.findAll();
	}
	
	public List<Curso> filtrar(CursoFilter filtro) {
		String nome = filtro.getNome() == null ? "%" : filtro.getNome();
		return cursos.findByNomeContaining(nome);
	}

	public void salvar(Curso curso) {
		cursos.save(curso);
	}
	
	public void excluir(Long codigo) {
		cursos.delete(codigo);
	}

}
