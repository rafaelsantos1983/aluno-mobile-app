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
 * Nota do Aluno por Curso
 * @author Eduardo Costa
 * @author Rafael Santos
 * @author Ranieri Queiroz
 * @author Roberta Costa
 * @since 28/07/2005
 */
public class Nota {
    /** Matricula do Aluno */
    public String matricula;
    /** Codigo da Disciplina */
    public String codigoDisciplina;
    /** Tipo da Nota */
    public String tipoNota;
    /** Valor da Nota */
    public String valor;

    /**
     * Nota
     */
    public Nota(){
        
    }
    
    /**
     * Cria a Nota
     * @param matricula Matricula do Aluno
     * @param codigoDisciplina Codigo da Disciplina
     * @param tipoNota Tipo da Nota
     * @param valor Valor da Nota
     */
    public Nota(String matricula, String codigoDisciplina, String tipoNota, String valor){
        this.matricula = matricula;
        this.codigoDisciplina =  codigoDisciplina;
        this.tipoNota =  tipoNota;
        this.valor = valor;
    }
    
    /**
     * Seta o valor na Nota em bytes 
     */
    public void fromBytes(byte[] registro){
        ByteArrayInputStream bais = new ByteArrayInputStream(registro);
        DataInputStream dis = new DataInputStream(bais);
        try {
            this.matricula = dis.readUTF();
            this.codigoDisciplina =  dis.readUTF();
            this.tipoNota = dis.readUTF();
            this.valor = dis.readUTF();
        } catch (IOException e) {
            System.out.println("Erro ao ler os bytes de Nota");
        }
    }

    /**
     * Ler os bytes da Nota
     * @return A nota em bytes
     */
	public byte[] toBytes() {
		byte data[] = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
            dos.writeUTF(matricula);
            dos.writeUTF(codigoDisciplina);
            dos.writeUTF(tipoNota);
            dos.writeUTF(valor);
			data = baos.toByteArray();
			baos.close();
			dos.close();
		} catch (IOException e) {
		    System.out.println("Erro ao gravar os bytes na Nota");
		}
		return data;
	}
}
