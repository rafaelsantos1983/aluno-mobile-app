/*
 * Criado em 28/07/2005
 * Copy Right 2005. Todos os direitos reservados a Eduardo Costa, Rafael Santos, Ranieri Queiroz e Roberta Costa.
 */
package aluno.tabela;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Aluno
 * @author Eduardo Costa
 * @author Rafael Santos
 * @author Ranieri Queiroz
 * @author Roberta Costa
 * @since 28/07/2005
 */
public class Aluno {
    /** Matricula do Aluno */
    public String matricula;
    /** Senha do Aluno */
    public String senha;
    /** Codigo da Disciplina */
    public String codigoDisciplina;
    /** Nome da Disciplina */
    public String nomeDisciplina;
    /** Codigo do Curso */
    public String codigoCurso;
    /** Descricao do Curso */
    public String descricaoCurso;
    
    /**
     * Aluno
     * @param matricula
     * @param senha
     * @param codigoDisciplina
     * @param nomeDisciplina
     * @param codigoCurso
     * @param descricaoCurso
     */
    public Aluno(String matricula,String senha, String codigoDisciplina, String nomeDisciplina,String codigoCurso, String descricaoCurso){
        this.matricula = matricula;
        this.senha =  senha;
        this.codigoDisciplina = codigoDisciplina;
        this.nomeDisciplina = nomeDisciplina;
        this.codigoCurso = codigoCurso;
        this.descricaoCurso = descricaoCurso;
    }
    
    /**
     * Aluno
     */
    public Aluno() {
    }

    /**
     * Seta o valor no Aluno em bytes 
     */
    public void fromBytes(byte[] registro){
        ByteArrayInputStream bais = new ByteArrayInputStream(registro);
        DataInputStream dis = new DataInputStream(bais);
        
        try {
            this.matricula = dis.readUTF();
            this.senha = dis.readUTF();
            this.codigoDisciplina = dis.readUTF();
            this.nomeDisciplina = dis.readUTF();
            this.codigoCurso = dis.readUTF();
            this.descricaoCurso = dis.readUTF();
        } catch (IOException e) {
            System.out.println("Erro ao ler os bytes de Aluno");
        }
    }

    /**
     * Ler os bytes do Aluno
     * @return Aluno em bytes
     */
	public byte[] toBytes() {
		byte data[] = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);

			dos.writeUTF(matricula);
			dos.writeUTF(senha);
			dos.writeUTF(codigoDisciplina);
			dos.writeUTF(nomeDisciplina);
			dos.writeUTF(codigoCurso);
			dos.writeUTF(descricaoCurso);
			data = baos.toByteArray();
			baos.close();
			dos.close();
		} catch (IOException e) {
		    System.out.println("Erro ao gravar os bytes no Aluno");
		}
		return data;
	}    
}