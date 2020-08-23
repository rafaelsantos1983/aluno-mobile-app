/*
 * Criado em 27/07/2005
 * Copyright 2005. Todos os direitos reservados a Eduardo Costa, Rafael Santos, Ranieri Queiroz e Roberta Costa.
 */

package aluno.sync;


import AlunoMobile;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;



public class MobileServer extends Thread {
	/** URL do Aluno Mobile Server */
    private String url;
    /** Insntancia do Display Principal do Aluno Mobile*/
	private AlunoMobile alunoMobile;
	/** Campos recebidos pelo servidor */
	public StringBuffer campos;
	
	/**
	 * Inicializa os atributos
	 * @param url URL do Aluno Mobile Server
	 * @param deliverer Classe principal do Aluno Mobile
	 */
	public MobileServer(String url, AlunoMobile alunoMobile) {
   		this.url = url;
	  	this.alunoMobile = alunoMobile;
	}
	
	/**
	 * Run da Thread
	 */
	public void run() {
		HttpConnection con = null;
		InputStream is = null;
		int status = -1;
		try {
		    con = (HttpConnection) Connector.open(url);
            con.setRequestMethod(HttpConnection.GET);
            status = con.getResponseCode();
            if( status == HttpConnection.HTTP_OK ){
                campos = new StringBuffer();
                is = con.openInputStream();
                int lido = is.read();
                lido = is.read();
                lido = is.read();
                while ( lido != -1 ){ //ler toda a string recebida pelo servidor
                    byte b1 = (byte) lido;
                    campos.append( (char) b1);
                    lido = is.read();
                }
            }
		} catch (IOException e1) {
               System.out.println("Erro de IO ao Receber os dados");
        }finally {
            try {
               is.close();
               con.close();
            } catch (IOException e) {
                System.out.println("Erro ao Fechar a Conexao");
            }
        }
	} 
}


 

