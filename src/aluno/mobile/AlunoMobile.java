/*
 * Criado em 23/07/2005
 * Copyright 2005. Todos os direitos reservados a Eduardo Costa, Rafael Santos, Ranieri Queiroz e Roberta Costa.
 */

package aluno.mobile;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import aluno.sync.MobileServer;

import java.io.IOException;

/**	
 * Cliente de Acesso ao Aluno Mobile Server
 * Permite a troca de dados com a aplicação AlunoMobile
 * @author Eduardo Costa
 * @author Rafael Santos
 * @author Ranieri Queiroz
 * @author Roberta Costa
 * @since 23/07/2005
 * @version 1.0
 */
public class AlunoMobile extends MIDlet implements CommandListener{
    /** Display*/
    private Display display;
    private Form tela;
    private TextField tf;
    private List lista;
    
    private Command cmdEnviar;
    private Command cmdReceber;
    private Command cmdSair;
    private Command cmdConfiguracao;
    private Command cmdAviso;
    private Command cmdNota;
    private Command cmdFalta;
    private Command cmdPTP;
    
    /** Tela principal da aplicação */
    private Form frmPrincipal;
    /** Tela de Configuracao do Aluno Mobile */
    private Form frmConfiguracao;
    /** Tela de Avisos */
    private Form frmAviso;
    /** Tela de Notas */
    private Form frmNota;
    /** Tela de Faltas */
    private Form frmFalta;
    /** Tela de PTP */
    private Form frmPTP;
    /** Visualiza a Data de Ultima Atualização */
    private StringItem dtUltimaAtualizacao;
    /** Visualiza a matricula do aluno */
    private StringItem matriculaAluno;
    /** Visualiza o curso do aluno */
    private StringItem cursoAluno;
    /** Matricula do Aluno */
    private TextField tfMatricula;
    /** Senha do Aluno */
    private TextField tfSenha;
    /** Comando para Voltar da Tela de Configuracao */
    private Command cmdVoltar;
    /** Comando de Salvar a Matricula e Senha */
    private Command cmdOk;
    /** Comando para atualizar a basde de dados de Avisos */
    private Command cmdAtualizarAvisos;
    /** Comando para atualizar a basde de dados de Nota */
    private Command cmdAtualizarNota;
    /** Comando para atualizar a basde de dados de Falta */
    private Command cmdAtualizarFalta;
    /** Comando para atualizar a basde de dados de PTP */
    private Command cmdAtualizarPTP;
    /** Nome do RecordStore de Aluno */	
    public static final String NAME_RECORDSTORE_ALUNO = "Aluno";
    /** Nome do RecordStore de Falta */
    public static final String NAME_RECORDSTORE_FALTA = "Falta";
    /** Nome do RecordStore de Aviso */
    public static final String NAME_RECORDSTORE_AVISO = "Aviso";
    /** Nome do RecordStore de Nota */
    public static final String NAME_RECORDSTORE_NOTA = "Nota";
    /** Nome do RecordStore de PTP */
    public static final String NAME_RECORDSTORE_PTP = "PTP";
    private StringItem dtUltimaAtualizacaoPTP;
    private StringItem siUltimaAtualizacaoPTP;
    private StringItem siTotalPTP;

    /**
     * Constutor Padrão
     * Cria uma mensagem no Form	
     */
    public AlunoMobile(){
        criarFormPrincipal();
        this.showAlert("Aluno Mobile - FIR","","/LogoAlunoMobile.png",null,frmPrincipal);
    }
    
    /**
     * Inicio da Aplicação
     */
    public void startApp() throws MIDletStateChangeException {
    }
    
    /**
     *  Trata o programa pausado
     */ 
    public void pauseApp(){
    }
   
   /**
    * Trata a saida do programa
    */
   protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
       display.setCurrent( null );
       this.notifyDestroyed();
   }//fim do método destroyApp()

   
   /**
    * Trata os comandos dos Form
    */
   public void commandAction(Command command, Displayable d ){
      if( command == cmdSair ){
          try {
              this.destroyApp(false);
              this.notifyDestroyed();
          } catch (MIDletStateChangeException e) {
              System.out.println("Erro ao Sair da Aplicação");
          }
      }else if(command == cmdConfiguracao){
          this.criarFormConfiguracao();
      }else if(command == cmdVoltar){
          display.setCurrent(frmPrincipal);
      }else if(command == cmdOk){
          this.putDadosFromDB(NAME_RECORDSTORE_ALUNO,null);
          this.criarFormPrincipal();
      }else if(command == cmdAviso){
          this.criarFormAviso();
      }else if(command == cmdAtualizarAvisos){
  		StringBuffer url = new StringBuffer();
		url.append(this.getAppProperty("Server-URL") + "?matsenha=");
		MobileServer caller = new MobileServer(url.toString(), this);
		caller.start();

      }else if(command == cmdNota){
          this.criarFormNota();
      }else if(command == cmdFalta){
          this.criarFormFalta();
      }else if(command == cmdPTP){
          this.criarFormPTP();
      }
   }

   /**
    * Cria um Alert parametrizado
    * @param txt
    * @param nameFile
    */
   public void showAlert(String rotulo,String mensagem, String nameFile,AlertType alert,Form form){
       Alert inicial = null;
       Image image = null;
       try {
           image = Image.createImage(nameFile);
           inicial = new Alert(rotulo,mensagem,image, alert);
       } catch (IOException e) {
           System.out.println("Erro ao Carregar o Arquivo, " + nameFile);
       }
       inicial.setTimeout(3000);
       display.setCurrent(inicial,form);
       inicial = null;
       image = null;
       System.gc();
   }
   
   /**
    * Cria o Form Principal da aplicação
    *
    */
   private void criarFormPrincipal(){
       if(frmPrincipal == null){
           frmPrincipal = new Form("Aluno Mobile - FIR");
           display = Display.getDisplay(this);
	       cmdSair     = new Command("Sair",Command.EXIT,0);
	       cmdConfiguracao = new Command("Configuração",Command.SCREEN,1);
	       cmdAviso  = new Command("Avisos",Command.SCREEN,2);
	       cmdNota  = new Command("Nota",Command.SCREEN,4);
	       cmdFalta  = new Command("Falta",Command.SCREEN,5);
	       cmdPTP = new Command("PTP",Command.SCREEN,6);
	       frmPrincipal.append(dtUltimaAtualizacao = new StringItem("Ult. Atualizacao:", "10/10/2000"));
	       frmPrincipal.append(matriculaAluno = new StringItem("Aluno:", "20041169073"));
		   frmPrincipal.append(cursoAluno = new StringItem("Curso:", "SI"));
		   frmPrincipal.addCommand(cmdSair);
		   frmPrincipal.addCommand(cmdConfiguracao);
		   frmPrincipal.addCommand(cmdAviso);
		   frmPrincipal.addCommand(cmdNota);
		   frmPrincipal.addCommand(cmdFalta);
		   frmPrincipal.addCommand(cmdPTP);

		   frmPrincipal.setCommandListener(this);
		   display.setCurrent(frmPrincipal);
       }
   }
   
   /**
    * Cria o Form da Tela de Configuracao
    */
   private void criarFormConfiguracao(){
       if(frmConfiguracao == null){
           System.out.println("entrou");
	       frmConfiguracao = new Form("Configuração - Aluno Mobile");
           
	       tfMatricula = new TextField("Matrícula:", "", 20,TextField.NUMERIC);
	       tfSenha = new TextField("Senha:", "", 20,TextField.PASSWORD);
	       cmdVoltar = new Command("Voltar",Command.BACK,0);
	       cmdOk = new Command("Ok",Command.SCREEN,1);
	            
	       frmConfiguracao.append(tfMatricula);
	       frmConfiguracao.append(tfSenha);
	       frmConfiguracao.addCommand(cmdOk);
	       frmConfiguracao.addCommand(cmdVoltar);
	       
	       frmConfiguracao.setCommandListener(this);
	       display.setCurrent(frmConfiguracao);
       }else{
	       display.setCurrent(frmConfiguracao);
       }
       this.setDadosInForm(NAME_RECORDSTORE_ALUNO);
   }

   
   /**
    * Cria o Form da Tela de Aviso
    */
   private void criarFormAviso(){
       if(frmAviso == null){
	       frmAviso = new Form("Aviso - Aluno Mobile");
	       cmdVoltar = new Command("Voltar",Command.BACK,0);
	       cmdAtualizarAvisos = new Command("Atualizar",Command.SCREEN,1);
	       frmAviso.addCommand(cmdAtualizarAvisos);
	       frmAviso.addCommand(cmdVoltar);
	       
	       frmAviso.setCommandListener(this);
	       display.setCurrent(frmAviso);	       
   	   }else{
	       display.setCurrent(frmAviso);
	   }
   }

   /**
    * Cria o Form da Tela de Nota
    */
   private void criarFormNota(){
       if(frmNota == null){
	       frmNota = new Form("Nota - Aluno Mobile");
	       cmdVoltar = new Command("Voltar",Command.BACK,0);
	       cmdAtualizarNota = new Command("Atualizar",Command.SCREEN,1);
	       
	       frmNota.addCommand(cmdAtualizarNota);
	       frmNota.addCommand(cmdVoltar);
	       
	       frmNota.setCommandListener(this);
	       display.setCurrent(frmNota);	       
       }else{
	       display.setCurrent(frmNota);
	   }
   }

   
   /**
    * Cria o Form da Tela de Falta
    */
   private void criarFormFalta(){
	   if(frmFalta == null){
	       frmFalta = new Form("Falta - Aluno Mobile");
	       cmdVoltar = new Command("Voltar",Command.BACK,0);
	       cmdAtualizarFalta = new Command("Atualizar",Command.SCREEN,1);
	       
	       frmFalta.addCommand(cmdAtualizarFalta);
	       frmFalta.addCommand(cmdVoltar);
	       
	       frmFalta.setCommandListener(this);
	       display.setCurrent(frmFalta);	       
   	   }else{
	       display.setCurrent(frmFalta);
	   }
   }
   
   /**
    * Cria o Form da Tela de PTP
    */
   private void criarFormPTP(){
       if(frmPTP == null){
	       frmPTP = new Form("PTP - Aluno Mobile");
	       display = Display.getDisplay(this);
	       
	       frmPrincipal.append(siUltimaAtualizacaoPTP = new StringItem("Ult. Atualizacao:", ""));
	       frmPrincipal.append(siTotalPTP = new StringItem("Total de Pontos:", ""));
	       
	       cmdVoltar = new Command("Voltar",Command.BACK,0);
	       cmdAtualizarPTP = new Command("Atualizar",Command.SCREEN,1);

	       frmPTP.addCommand(cmdAtualizarNota);
	       frmPTP.addCommand(cmdVoltar);
	       
	       frmPTP.setCommandListener(this);
	       display.setCurrent(frmPTP);	       
	   }else{
	       display = Display.getDisplay(this);
	       frmPTP.setCommandListener(this);
	       display.setCurrent(frmPTP);
	   }
   }

   /**
    * Grava os Dados no celular
    * @param nameRecordStore Nome do RecordSotore a ser armazenado os dados
    * @return
    */
   private void putDadosFromDB(String nameRecordStore, byte[] dados) {
       RecordStore rs = null;
   	   try {
     	   rs = RecordStore.openRecordStore(nameRecordStore, true);
           if (rs != null) {
               if(nameRecordStore.equals(NAME_RECORDSTORE_ALUNO)){ //Altear sempre o registor 0 do Aluno no celular
                   String registro = tfMatricula.getString().trim()+ "|" + tfSenha.getString().trim() + "|" + cursoAluno.getText().trim();
                   if(rs.getNumRecords() == 0){
                       rs.addRecord(registro.getBytes(),0,registro.length());
                   }else{
                       rs.setRecord(1,registro.getBytes(),0,registro.length());
                   }
                   this.showAlert("Sucesso","Aluno Registrado com sucesso","/check.png",AlertType.CONFIRMATION,frmConfiguracao);
   			   }
   		       if(nameRecordStore.equals(NAME_RECORDSTORE_AVISO)){
   		            
   		   	   }
   		       if(nameRecordStore.equals(NAME_RECORDSTORE_FALTA)){
   		            
   		       }
   		       if(nameRecordStore.equals(NAME_RECORDSTORE_NOTA)){
   		            
   		       }
   		       if(nameRecordStore.equals(NAME_RECORDSTORE_PTP)){
   		            
   		       }
               //Deserializa o pedido a partir do registro
               //order.fromBytes(rs.getRecord(1));
               //return order;
               }
   	   }catch (RecordStoreFullException e) {
   	       System.out.println("Erro Ao gravar os dados  - Record Store Full de "+nameRecordStore);
   	    e.printStackTrace();
       } catch (RecordStoreNotFoundException e) {
           System.out.println("Erro Ao gravar os dados  - Record Stroe Não Encontrado de "+nameRecordStore);
           e.printStackTrace();
       } catch (RecordStoreException e) {
           System.out.println("Erro Ao gravar os dados - Record Store de " + nameRecordStore);
           e.printStackTrace();
       }finally {
           try {
               if(rs != null){
                   rs.closeRecordStore();
               }
           } catch (RecordStoreNotOpenException e1) {
               System.out.println("Erro Ao gravar os dados quando fechar - Record Stroe Não Aberto de "+nameRecordStore);
           } catch (RecordStoreException e1) {
               System.out.println("Erro Ao gravar os dados quando fechar - Record Store de " + nameRecordStore);
           }
     	}
   	}
   	
   	/**
   	 * Seta os dados no Form de acordo com a Funcionalidade
   	 * @param nameRecordStore Nome do RecordStore
   	 */  
   	private void setDadosInForm(String nameRecordStore) {
        RecordStore rs = null;
    	   try {
    	       rs = RecordStore.openRecordStore(nameRecordStore, false);
    	       if (rs != null) {
    	           if(nameRecordStore.equals(NAME_RECORDSTORE_ALUNO)){ //Altear sempre o registor 0 do Aluno no celular
    	               	//transforma os dados em String
    	                String str = new String(rs.getRecord(1));
    	                int i = 0;
    	              	i = str.indexOf("|");
    	              	tfMatricula.setString(str.substring(0,i));
    	              	tfSenha.setString(str.substring(i+1,str.indexOf("|",i+1)));
    	              	if(rs.getNumRecords() > 0){
    	              	    i = str.indexOf("|",i+1);
    	              	    cursoAluno.setText(str.substring(i+1,str.length()));
    	              	}
    	           }
    	           if(nameRecordStore.equals(NAME_RECORDSTORE_AVISO)){
    		            
    		   	   }
    	           if(nameRecordStore.equals(NAME_RECORDSTORE_FALTA)){
    		            
    	           }
    	           if(nameRecordStore.equals(NAME_RECORDSTORE_NOTA)){
    		            
    	           }
    	           if(nameRecordStore.equals(NAME_RECORDSTORE_PTP)){
    		            
    	           }
                }
    	   }catch (RecordStoreFullException e) {
    	       System.out.println("Erro - Record Store Full ao Ler o "+nameRecordStore);
        } catch (RecordStoreNotFoundException e) {
            System.out.println("Erro - Record Stroe Não Encontrado ao Ler o "+nameRecordStore);
        } catch (RecordStoreException e) {
            System.out.println("Erro - Record Store ao Ler o " + nameRecordStore);
        }finally {
            try {
                if(rs != null){
                    rs.closeRecordStore();
                }
            } catch (RecordStoreNotOpenException e1) {
                System.out.println("Erro - Record Stroe Não Aberto quando fechar ao Ler o "+nameRecordStore);
            } catch (RecordStoreException e1) {
                System.out.println("Erro - Record Store quando fechar ao Ler o  " + nameRecordStore);
            }
      	}
   	}
   	
   /**
    * Deleta o RecordStore
    * @param nameRecordStore
    */
   	private void deleteOrderFromDB(String nameRecordStore) {
   	    if(RecordStore.listRecordStores() != null){
   	        try {
   	            RecordStore.deleteRecordStore(nameRecordStore);
   	        } catch (Exception e) {
   	            System.out.println("Erro ao deletar o Record Store, "+nameRecordStore);
   	        }
   	    }
   	}
}