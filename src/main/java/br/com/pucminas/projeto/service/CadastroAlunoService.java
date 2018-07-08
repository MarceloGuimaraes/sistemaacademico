package br.com.pucminas.projeto.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



import br.com.pucminas.projeto.model.Aluno;
import br.com.pucminas.projeto.repository.Alunos;
import br.com.pucminas.projeto.repository.filter.AlunoFilter;

@Service
public class CadastroAlunoService {
	
	@Autowired
	private Alunos alunos;
	
	
	public void salvar(Aluno aluno) {
		
		alunos.save(aluno);
		
	}
	
	public void excluir (Long codigo) {
		alunos.delete(codigo);
	}
	
	public List<Aluno> filtrar(AlunoFilter filtro) {
		String nome = filtro.getNome() == null ? "%" : filtro.getNome();
		return alunos.findByNomeContaining(nome);
	}
	
	public List<Aluno> findAll() {
		return alunos.findAll();
	}

}
