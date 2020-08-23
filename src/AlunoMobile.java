/*
 * Criado em 23/07/2005
 * Copyright 2005. Todos os direitos reservados a Eduardo Costa, Rafael Santos, Ranieri Queiroz e Roberta Costa.
 */

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;
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
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

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
public class AlunoMobile extends MIDlet implements CommandListener,Runnable{
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
    private Form frmConectando;
    private Gauge gaProgress;
    private TempoConectando tc;
    
    private Thread threadSplash;
    private Form frmSplash;
    private Image iLogoAlunoMobile = null;
    private ImageItem imgItem;
    private StringItem siUltimaAtualizacaoFalta;
    private StringItem siUltimaAtualizacaoNota;
    private StringItem siUltimaAtualizacaoAviso;

    /**
     * Constutor Padrão
     * Cria uma mensagem no Form	
     */
    public AlunoMobile(){
        criarFormPrincipal();
        criaFormSplash();
//        this.showAlert("Aluno Mobile - FIR","","/LogoAlunoMobile.png",null,frmPrincipal);
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
              System.out.println("Erro ao Sair da Aplicação Quando clicado no botão Sair");
          }
      }else if(command == cmdConfiguracao){
          this.criarFormConfiguracao();
      }else if(command == cmdVoltar){
          this.display.setCurrent(frmPrincipal);
      }else if(command == cmdOk){
          this.putDadosFromDB(NAME_RECORDSTORE_ALUNO,null);
          this.criarFormPrincipal();
      }else if(command == cmdAviso){
          this.criarFormAviso();
      }else if(command == cmdAtualizarPTP){
  		  StringBuffer url = new StringBuffer("123");
  		  System.out.println("tfMatricula.getString().trim()+tfSenha.getString().trim()="+getMatricula().trim()+getSenha().trim());
		  url.append(this.getAppProperty("Server-URL") + "?matsenha=04|"+getMatricula()+"|"+getSenha());
		  System.out.println("url.toString()="+url.toString());
		  MobileServer caller = new MobileServer(url.toString(), this);
		  caller.start();
		  /*try {
            Thread.sleep(10000);
          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          */
          System.out.println("gaProgress="+gaProgress);
          //gaProgress.setLabel("Fim");
          //tc.cancel();
          System.out.println("caller.campos.toString()="+caller.campos);
		  //putDadosFromDB(NAME_RECORDSTORE_PTP,caller.campos.toString());
		  
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
           if(!(nameFile.equals("")))
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
           cmdSair         = new Command("Sair",Command.EXIT,0);
	       cmdConfiguracao = new Command("Configuração",Command.SCREEN,1);
	       cmdAviso        = new Command("Avisos",Command.SCREEN,2);
	       cmdNota         = new Command("Nota",Command.SCREEN,4);
	       cmdFalta        = new Command("Falta",Command.SCREEN,5);
	       cmdPTP          = new Command("PTP",Command.SCREEN,6);
	       frmPrincipal.append(dtUltimaAtualizacao = new StringItem("Atualizacao:", "10/10/2000"));
	       frmPrincipal.append(matriculaAluno = new StringItem("Aluno:", "20041169073"));
		   frmPrincipal.append(cursoAluno = new StringItem("Curso:", "SI"));
		   frmPrincipal.addCommand(cmdSair);
		   frmPrincipal.addCommand(cmdConfiguracao);
		   frmPrincipal.addCommand(cmdAviso);
		   frmPrincipal.addCommand(cmdNota);
		   frmPrincipal.addCommand(cmdFalta);
		   frmPrincipal.addCommand(cmdPTP);
		   frmPrincipal.setCommandListener(this);
		   //display.setCurrent(frmPrincipal);
       }
   }
   
   /**
    * Cria o Form da Tela de Configuracao
    */
   private void criarFormConfiguracao(){
       if(frmConfiguracao == null){
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
	       
	       frmAviso.append(siUltimaAtualizacaoAviso = new StringItem("Atualização:", ""));
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
	       
	       frmNota.append(siUltimaAtualizacaoNota = new StringItem("Atualização:", ""));
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
	       frmFalta.append(siUltimaAtualizacaoFalta = new StringItem("Atualização:", ""));

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
	       frmPTP.append(siUltimaAtualizacaoPTP = new StringItem("Atualização:", ""));
	       frmPTP.append(siTotalPTP = new StringItem("Pontos:", ""));
	       
	       cmdVoltar = new Command("Voltar",Command.BACK,0);
	       cmdAtualizarPTP = new Command("Atualizar",Command.SCREEN,1);

	       frmPTP.addCommand(cmdAtualizarPTP);
	       frmPTP.addCommand(cmdVoltar);
	       
	       frmPTP.setCommandListener(this);
	       display.setCurrent(frmPTP);	       
	   }else{
	       frmPTP.setCommandListener(this);
	       display.setCurrent(frmPTP);
	   }
   }
   
   /**
    * Cria o Form com o Splsh da aplicação, apresentando o LogoTipo do Aluno Mobile
    */
   private void criaFormSplash(){
       frmSplash = new Form("Aluno Mobile - FIR");
	   try {
		   iLogoAlunoMobile = Image.createImage("/LogoAlunoMobile.png");
	   }catch (Exception e) {
	       System.err.println("Erro de IO ao carregar a Imagem no splash");
	   }
	   imgItem = new ImageItem("", iLogoAlunoMobile, ImageItem.LAYOUT_CENTER, "");
	   frmSplash.append(imgItem);
	   frmSplash.setCommandListener(this);
	   threadSplash = new Thread(this);
	   threadSplash.start();
   }
   
   /**
    * Run da Thread
    */
   public void run(){
		display.setCurrent(frmSplash);
		try {
			Thread.sleep(3000); // espera 3 segundos
		}
		catch (Exception e) {
			System.err.println("Erro ao Excutar o Timer do Splash");
		}
		display.setCurrent(frmPrincipal);
		frmSplash = null;
		iLogoAlunoMobile = null;
		threadSplash = null;
		imgItem = null;
		System.gc();
	}
   
   
   /**
    * Cria o Form para visualizar o Download dos Dados
    */
   private void criarFormConnectando(){
       if(frmConectando == null){
           gaProgress = new Gauge("Progresso do Download", false, 20 ,1);
           frmConectando =  new Form("Atualizacao - Aluno Mobile");
           frmConectando.append(gaProgress);
           frmConectando.setCommandListener(this);
           display.setCurrent(frmConectando);
	   }else{
	       frmConectando.setCommandListener(this);
	       display.setCurrent(frmConectando);
	   }
   }

   /**
    * Grava os Dados no celular
    * @param nameRecordStore Nome do RecordSotore a ser armazenado os dados
    * @return
    */
   private void putDadosFromDB(String nameRecordStore, String dados) {
       RecordStore rs = null;
   	   try {
     	   rs = RecordStore.openRecordStore(nameRecordStore, true);
           if (rs != null) {
               if(nameRecordStore.equals(NAME_RECORDSTORE_ALUNO)){ //Altear sempre o registor 0 do Aluno no celular
                   String registro = tfMatricula.getString().trim()+ "|" + tfSenha.getString().trim() + "|" + cursoAluno.getText().trim();
                   if(rs.getNumRecords() == 0){
                       rs.addRecord(registro.getBytes(),0,registro.length());
                   }else{
                       if(getMatricula().equals(tfMatricula.getString().trim())){
                           rs.setRecord(1,registro.getBytes(),0,registro.length());
                       }else{
                           rs.setRecord(1,registro.getBytes(),0,registro.length());
                           this.deleteOrderFromDB(NAME_RECORDSTORE_AVISO);
                           this.deleteOrderFromDB(NAME_RECORDSTORE_FALTA);
                           this.deleteOrderFromDB(NAME_RECORDSTORE_NOTA);
                           this.deleteOrderFromDB(NAME_RECORDSTORE_PTP);
                           this.showAlert("Atenção","Novo Aluno. \nMatricula Alterada.","",AlertType.WARNING,frmPrincipal);
                           return;
                       }
                   }
                   this.showAlert("Sucesso","Aluno Registrado com sucesso","/check.png",AlertType.CONFIRMATION,frmPrincipal);
   			   }
   		       if(nameRecordStore.equals(NAME_RECORDSTORE_AVISO)){
   		   	   }
   		       if(nameRecordStore.equals(NAME_RECORDSTORE_FALTA)){
   		            
   		       }
   		       if(nameRecordStore.equals(NAME_RECORDSTORE_NOTA)){
   		       }
   		       if(nameRecordStore.equals(NAME_RECORDSTORE_PTP)){
                   if(rs.getNumRecords() == 0){
                       rs.addRecord(dados.getBytes(),0,dados.length());
                   }else{
                       rs.setRecord(1,dados.getBytes(),0,dados.length());
                   }
                   this.showAlert("Sucesso","Dados do PTP Atualizado","/check.png",AlertType.CONFIRMATION,frmPTP);    
   		       }
           }
   	   }catch (Exception e) {
   	       System.out.println("Erro Ao gravar os dados de "+nameRecordStore);
       }finally {
           try {
               if(rs != null){
                   rs.closeRecordStore();
               }
           } catch (Exception e1) {
               System.out.println("Erro Ao gravar os dados de "+nameRecordStore);
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
    	           if(nameRecordStore.equals(NAME_RECORDSTORE_ALUNO)){ 
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
        }catch (Exception e) {
    	       System.out.println("Erro - Quando Seta os dados Ao Ler o "+nameRecordStore);
        }finally {
            try {
                if(rs != null){
                    rs.closeRecordStore();
                }
            } catch (Exception e1) {
                System.out.println("Erro - Quando Seta os dados Quando fechar ao Ler o "+nameRecordStore);
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
   	
   	/**
   	 * Pega a Matricula do Aluno
   	 * @return Matricula do Aluno
   	 */
   	private String getMatricula(){
   	    RecordStore rs = null;
 	    try {
 	        rs = RecordStore.openRecordStore(NAME_RECORDSTORE_ALUNO, false);
 	        if (rs != null) {
 	            String str = new String(rs.getRecord(1));
                int i = 0;
             	i = str.indexOf("|");
             	return str.substring(0,i);
 	        }        
        }catch (Exception e) {
	        System.out.println("Erro - Ao Pegar a Matricula "+NAME_RECORDSTORE_ALUNO);
        }finally {
            try {
                if(rs != null){
                    rs.closeRecordStore();
                }
            } catch (Exception e1) {
                System.out.println("Erro -  Ao fechar ao Pegar a Matricula "+NAME_RECORDSTORE_ALUNO);
            }
        }
   	    return null;
   	}

   	
   	/**
   	 * Pega a Senha do Aluno
   	 * @return Senha do Aluno
   	 */
   	private String getSenha(){
   	    RecordStore rs = null;
 	    try {
 	        rs = RecordStore.openRecordStore(NAME_RECORDSTORE_ALUNO, false);
 	        if (rs != null) {
 	            String str = new String(rs.getRecord(1));
                int i = 0;
             	i = str.indexOf("|");
             	return (str.substring(i+1,str.indexOf("|",i+1)));
 	        }        
        }catch (Exception e) {
	        System.out.println("Erro - Ao Pegar a Senha "+NAME_RECORDSTORE_ALUNO);
        }finally {
            try {
                if(rs != null){
                    rs.closeRecordStore();
                }
            } catch (Exception e1) {
                System.out.println("Erro -  Ao fechar ao Pegar a Senha "+NAME_RECORDSTORE_ALUNO);
            }
        }
   	    return null;
   	}   	
   	
   	
   	public class TempoConectando extends TimerTask{
   	    /* (non-Javadoc)
   	     * @see java.util.TimerTask#run()
   	     */
   	    public void run() {
   	        System.out.println("Entrou no Run do Timer");
   	        if(gaProgress.getValue() < gaProgress.getMaxValue() + 1){
   	            gaProgress.setValue(gaProgress.getValue() + 1);
   	        }else{
   	            gaProgress.setValue(1);
   	        }
   	    }
   	}
   	
   	/**
   	 * Cliente do Servidor do Aluno Mobile
   	 * @author mobile
   	 * @since 02/08/2005
   	 */
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
   			/*criarFormConnectando();
   			Timer timer = new Timer();
   			tc = new TempoConectando();
   			timer.scheduleAtFixedRate(tc, 0, 1000);
   			*/
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
   	                System.out.println("campos="+campos.toString());
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
}