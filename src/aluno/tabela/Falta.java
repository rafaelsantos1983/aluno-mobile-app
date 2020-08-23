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
 * Falta do Aluno Por Disciplina
 * @author Eduardo Costa
 * @author Rafael Santos
 * @author Ranieri Queiroz
 * @author Roberta Costa
 * @since 28/07/2005
 */
public class Falta {
    /** Matricula do Aluno */
    public String matricula;
    /** Codigo da Disciplina */ 
    public String codigoDisciplina;
    /** Mes das Faltas */
    public String mes;
    /** Quantidade de falta */
    public String quantidade;
    
    /**
     * Falta
     */
    public Falta(){
    }
    
    /**
     * Falta
     * @param matricula Matricula dpo Aluno
     * @param codigoDisciplina Codigo da Disciplina
     * @param mes Mes das Falta
     * @param quantidade Quantidade de faltas
     */
    public Falta(String matricula, String codigoDisciplina, String mes, String quantidade){
        this.matricula = matricula;
        this.codigoDisciplina = codigoDisciplina;
        this.mes = mes;
        this.quantidade =  quantidade;
    }
    
    /**
     * Seta o valor na Falta em bytes 
     */
    public void fromBytes(byte[] registro){
        ByteArrayInputStream bais = new ByteArrayInputStream(registro);
        DataInputStream dis = new DataInputStream(bais);
        try {
            this.matricula = dis.readUTF();
            this.codigoDisciplina =  dis.readUTF();
            this.mes = dis.readUTF();
            this.quantidade = dis.readUTF();
        } catch (IOException e) {
            System.out.println("Erro ao ler os bytes de Falta");
        }
    }
    
    /**
     * Ler os bytes da Falta
     * @return A falta em bytes
     */
	public byte[] toBytes() {
		byte dados[] = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
            dos.writeUTF(matricula);
            dos.writeUTF(codigoDisciplina);
            dos.writeUTF(mes);
            dos.writeUTF(quantidade);
			dados = baos.toByteArray();
			baos.close();
			dos.close();
		} catch (IOException e) {
		    System.out.println("Erro ao gravar os bytes na Falta");
		}
		return dados;
	}
}