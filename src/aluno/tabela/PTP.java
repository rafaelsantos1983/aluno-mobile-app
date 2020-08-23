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
 * PTP - 
 * @author Eduardo Costa
 * @author Rafael Santos
 * @author Ranieri Queiroz
 * @author Roberta Costa
 * @since 28/07/2005
 */
public class PTP {
    /** Matricula do Aluno */
    public String matricula;
    
    /** Total de Pontos do PTP*/
    public int totalPontos;
    
    /**
     * PTP
     */
    public PTP(){
    }
    
    /**
     * Cria PTP 
     * @param matricula Matricula do Aluno
     * @param totalPontos Total de pontos do PTP
     */
    public PTP(String matricula,int totalPontos){
        this.matricula = matricula;
        this.totalPontos =  totalPontos;
    }

    /**
     * Seta o valor no PTP em bytes 
     */
    public void fromBytes(byte[] registro){
        ByteArrayInputStream bais = new ByteArrayInputStream(registro);
        DataInputStream dis = new DataInputStream(bais);
        
        try {
            this.matricula = dis.readUTF();
            this.totalPontos = dis.readInt();
        } catch (IOException e) {
            System.out.println("Erro ao ler os bytes de PTP");
        }
    }

    /**
     * Ler os bytes do PTP
     * @return PTP em bytes
     */
	public byte[] toBytes() {
		byte data[] = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
            dos.writeUTF(matricula);
			dos.writeInt(totalPontos);
			data = baos.toByteArray();
			baos.close();
			dos.close();
		} catch (IOException e) {
		    System.out.println("Erro ao gravar os bytes no PTP");
		}
		return data;
	}
}