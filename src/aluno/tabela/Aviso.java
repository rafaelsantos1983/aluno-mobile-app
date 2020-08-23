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
 * Aviso ao Aluno
 * @author Eduardo Costa
 * @author Rafael Santos
 * @author Ranieri Queiroz
 * @author Roberta Costa
 * @since 28/07/2005
 */
public class Aviso {
    /** Matricla do Aluno */
    public String matricula;
    /** Data de publicação do Aviso*/
    public String data;
    /** Texto do Aviso*/
    public String mensagem;
    
    /**
     * Aviso
     */
    public Aviso(){
        
    }
    
    /**
     * Aviso
     * @param matricula Matricula do Aluno
     * @param data Data de Publicacao do Aviso
     * @param mensagem Texto do Aviso
     */
    public Aviso(String matricula, String data,String mensagem){
        this.matricula = matricula;
        this.data =  data;
        this.mensagem = mensagem;
    }
    
    /**
     * Seta o valor na Aviso em bytes 
     */
    public void fromBytes(byte[] registro){
        ByteArrayInputStream bais = new ByteArrayInputStream(registro);
        DataInputStream dis = new DataInputStream(bais);
        try {
            this.matricula = dis.readUTF();
            this.data =  dis.readUTF();
            this.mensagem = dis.readUTF();
        } catch (IOException e) {
            System.out.println("Erro ao ler os bytes de Aviso");
        }
    }
    
    /**
     * Ler os bytes da Aviso
     * @return O aviso em bytes
     */
	public byte[] toBytes() {
		byte dados[] = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
            dos.writeUTF(matricula);
            dos.writeUTF(data);
            dos.writeUTF(mensagem);
			dados = baos.toByteArray();
			baos.close();
			dos.close();
		} catch (IOException e) {
		    System.out.println("Erro ao gravar os bytes no Aviso");
		}
		return dados;
	}
}