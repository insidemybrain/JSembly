package com.jsembly.menu;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import com.jsembly.extras.Utilidades;
import com.jsembly.funcoes.ArithmeticLogicUnit;
import com.jsembly.funcoes.ConversaoBase;
import com.jsembly.main.ArraysLists;
import com.jsembly.main.Janela;
import com.jsembly.main.Memoria;
import com.jsembly.mips.Registrador;
import com.jsembly.mips.TipoInstrucao;

public class StepByStep {

	public StepByStep(){
		boolean erro = false;
		int codigoErro = 0;
		int linhaAtual = 0;
		int regSalvar = 0;
		String valorReg = null;
		int decimal;
			String binario = null;
		ArraysLists.arrLabel.clear();
		String lm = "Sem Linguagem de M�quina";
		Registrador.LimparAtividade(Janela.dtm);
		Janela.dtmExec.setRowCount(0);
		Memoria.LimparMemoria(Janela.dtmMem);
		Janela.dtmMem.fireTableDataChanged();
		Janela.painelLinguagemMaquina.setText("");
		
		ArrayList<String> linhasLidas = Utilidades.LerArquivo(Janela.temp.getAbsolutePath());
		Pattern operador = Pattern.compile("\\w+", Pattern.CASE_INSENSITIVE);
		Pattern registrador = Pattern.compile("[$]\\w+", Pattern.CASE_INSENSITIVE);
		Pattern endereco = Pattern.compile(" \\w+|[0-9]|\\,w+|,[0-9]", Pattern.CASE_INSENSITIVE);
		Pattern enderecoTipoI = Pattern.compile(",\\w+|,[0-9]", Pattern.CASE_INSENSITIVE);
		Pattern label = Pattern.compile("\\b\\w{1,8}[:]", Pattern.CASE_INSENSITIVE);
		
		// Primeiro busca todas as labels do arquivo
		//  e adiciona elas ao ArrayLIst & � Mem�ria
		for(String labelLida : linhasLidas){
			Matcher matcherLbl = label.matcher(labelLida);
			if(matcherLbl.find() && erro != true){
				String lbl = matcherLbl.group().substring(matcherLbl.start(),matcherLbl.end()-1);
				for(int i = 0; i < ArraysLists.arrLabel.size(); i ++){
					if(ArraysLists.arrLabel.get(i).equals(lbl)){
						JOptionPane.showMessageDialog(null,
							"<html>"
							+ "Prezado usu�rio, a label: <b style='color:red;'>"+lbl+"</b> j� foi definida anteriormente no sistema.<br>"
							+ "<i>Favor revisar as informa��es digitadas.</i>"
							+ "</html>",
							"Label duplicada",
							JOptionPane.ERROR_MESSAGE);
						ArraysLists.arrLabel.remove(lbl);
						erro = true;
						codigoErro = 1;
					}
				}
				ArraysLists.arrLabel.add(lbl);
			}
		}
		// Ap�s encontrar todas a labels definidas
		//  inicia uma busca por operadores,registradores, etc
		if(erro){
			JOptionPane.showMessageDialog(null,
					"<html>"
					+ "Prezado usu�rio, o sistema n�o poder� continuar a execu��o do c�digo,<br>"
					+ "pois o mesmo apresenta um erro.<br><br>"
					+ "<b style='color:red;'>C�digo: "+codigoErro+"</b><br><br>"
					+ "<i>Favor revisar as informa��es digitadas ou informar o c�digo ao desenvolvedor.</i>"
					+ "</html>",
					"Erro na Compila��o",
					JOptionPane.ERROR_MESSAGE);
		} else {
		for(String linha : linhasLidas){
			linhaAtual++;
			ArraysLists.regEncontrados.clear();
			
			Matcher matcherLbl = label.matcher(linha);
			if(matcherLbl.find()){
				String lbl = matcherLbl.group().substring(matcherLbl.start(),matcherLbl.end()-1);
				while(lbl.length() < 8){
					lbl += " ";
				}
				Janela.lblOito = lbl;
				while(lbl.length() < 32){
					lbl += "0";
				}
				Memoria.AlocarMemoria(lbl,Janela.dtmMem);
			}
			// Busca de Operadores
		    Matcher matcher = operador.matcher(linha);
		    if(matcher.find()) {
			    // Busca de Registradores
			    Matcher matcher2 = registrador.matcher(linha);
			    while(matcher2.find()) {
			      for(int i = 0; i < ArraysLists.registradores.size(); i++){
			    	  if(matcher2.group().toLowerCase().equals(ArraysLists.registradores.get(i).toString())){
			    		  ArraysLists.regEncontrados.add(ArraysLists.registradores.get(i));
			    		  break;
			    	  }
			      }
			    }
		      for(int i = 0; i < ArraysLists.operadores.size(); i++){
		    	  if(matcher.group().toLowerCase().equals(ArraysLists.operadores.get(i).toString())){
		    		  
	    		  	  String enderecoOuLabel = null;
	    		  	  String endLbl = null;
	    		  	  int end = 0;
	    		  	  if(ArraysLists.operadores.get(i).getId() >= 999){
    		  				JOptionPane.showMessageDialog(
    		  						null,
    		  						"<html>"
    		  						+ "Lamentamos, mas o operador <b style='font-size:9px; color: red;'>"+ArraysLists.operadores.get(i)+"</b>, localizado na linha <b style='font-size:9px; color: red;'>"+linhaAtual+"</b>,<br>"
    		  						+ " n�o poder� ser executado.<br>"
    		  						+ "O mesmo ainda n�o foi completamente habilitado.<br>"
    		  						+ "<i>Tente utiliz�-lo na pr�xima revis�o do sistema.</i>"
    		  						+ "</html>",
    		  						"Operador n�o implementado",
    		  						JOptionPane.ERROR_MESSAGE);
    		  				break;
	    		  		}
		    		  switch(ArraysLists.operadores.get(i).getTipoIntrucao()){
		    		  
		    		  	// -- TIPO I
		    		  	case 0:
		    		  		ArraysLists.regEncontrados.get(0).setAtivo(true);
		    		  		Registrador.AtualizarAtividade(Janela.dtm);
		    		  			Matcher matcher3 = enderecoTipoI.matcher(linha);
								if(matcher3.find()) { enderecoOuLabel = matcher3.group().substring(1); }
		    		  				switch(ArraysLists.operadores.get(i).getId()){
		    		  				// ADDI
		    		  				case 2:
		    		  					decimal = Integer.parseInt(enderecoOuLabel, 10);
				    		  			binario = Integer.toBinaryString(decimal);
				    		  			while(binario.length()<16){
				    		  				binario = "0" + binario ;
				    		  				lm = TipoInstrucao.InstrucaoTipoI(ArraysLists.operadores.get(i).getValorBits(),ArraysLists.regEncontrados.get(0).getValorBits(),ArraysLists.regEncontrados.get(1).getValorBits(),binario);
				    		  			}
				    		  			if(lm.length() > 32){
				    		  				JOptionPane.showMessageDialog(null,
				    		  						"<html>"
				    		  						+ "Overflow na linha: "
				    		  						+ "<b style='color:red;'>"+linhaAtual+"</b>"
				    		  						+ "</html>", "Detectado overflow!", JOptionPane.ERROR_MESSAGE);
				    		  			}
		    		  					for(int r = 0; r < Janela.dtm.getRowCount(); r++){
					    		  			if(Janela.dtm.getValueAt(r, 0).equals(ArraysLists.regEncontrados.get(1).toString())){
					    		  				valorReg = Janela.dtm.getValueAt(r, 2).toString();
					    		  			}
					    		  		}
		    		  					Memoria.AlocarMemoria(lm, Janela.dtmMem);
		    		  					Janela.painelLinguagemMaquina.append(lm+"\n");
		    		  					Janela.dtmExec.addRow(new Object[]{
		    		  							Memoria.BuscarEndereco(lm.substring(0, 8), Janela.dtmMem),
		    		  							"0x"+ConversaoBase.converteBinarioParaHexadecimal(lm),
		    		  							ArraysLists.operadores.get(i)+" $"+ArraysLists.regEncontrados.get(0).getId()+",$"+ArraysLists.regEncontrados.get(1).getId()+","+enderecoOuLabel,
		    		  							linhaAtual+": "+ArraysLists.operadores.get(i)+" "+ArraysLists.regEncontrados.get(0).toString()+","+ArraysLists.regEncontrados.get(1).toString()+","+enderecoOuLabel});
					    		  		regSalvar = ArithmeticLogicUnit.addi(Integer.parseInt(valorReg,10), decimal);
			    		  				for(int r = 0; r < Janela.dtm.getRowCount(); r++){
			    		  					if(Janela.dtm.getValueAt(r, 0).equals(ArraysLists.regEncontrados.get(0).toString())){
			    		  						Janela.dtm.setValueAt(regSalvar, r, 2);
			    		  					}
			    		  				}
		    		  				break;
		    		  				// LUI
		    		  				case 9:
		    		  					while(lm.length()<32){
		    		  						binario = "0" + binario ;
		    		  						lm = TipoInstrucao.InstrucaoTipoI(ArraysLists.operadores.get(i).getValorBits(),ArraysLists.regEncontrados.get(0).getValorBits(),binario);
				    		  			}
				    		  			if(lm.length() > 32){
				    		  				JOptionPane.showMessageDialog(null,
				    		  						"<html>"
				    		  						+ "Overflow na linha: "
				    		  						+ "<b style='color:red;'>"+linhaAtual+"</b>"
				    		  						+ "</html>", "Detectado overflow!", JOptionPane.ERROR_MESSAGE);
				    		  			}
		    		  					Memoria.AlocarMemoria(lm, Janela.dtmMem);
		    		  					Janela.painelLinguagemMaquina.append(lm+"\n");
		    		  					Janela.dtmExec.addRow(new Object[]{
		    		  							Memoria.BuscarEndereco(lm.substring(0, 8), Janela.dtmMem),
		    		  							"0x"+ConversaoBase.converteBinarioParaHexadecimal(lm),
		    		  							ArraysLists.operadores.get(i)+" $"+ArraysLists.regEncontrados.get(0).getId()+","+enderecoOuLabel,
		    		  							linhaAtual+": "+ArraysLists.operadores.get(i)+" "+ArraysLists.regEncontrados.get(0).toString()+","+enderecoOuLabel});
					    		  		regSalvar = ConversaoBase.converteBinarioParaDecimal(binario);
			    		  				for(int r = 0; r < Janela.dtm.getRowCount(); r++){
			    		  					if(Janela.dtm.getValueAt(r, 0).equals(ArraysLists.regEncontrados.get(0).toString())){
			    		  						Janela.dtm.setValueAt(regSalvar, r, 2);
			    		  					}
			    		  				}
		    		  				break;
		    		  				// SLTI
		    		  				case 20:
		    		  					decimal = Integer.parseInt(enderecoOuLabel, 10);
				    		  			binario = Integer.toBinaryString(decimal);
				    		  			while(binario.length()<16){
				    		  				binario = "0" + binario ;
				    		  				lm = TipoInstrucao.InstrucaoTipoI(ArraysLists.operadores.get(i).getValorBits(),ArraysLists.regEncontrados.get(0).getValorBits(),ArraysLists.regEncontrados.get(1).getValorBits(),binario);
				    		  			}
				    		  			if(lm.length() > 32){
				    		  				JOptionPane.showMessageDialog(null,
				    		  						"<html>"
				    		  						+ "Overflow na linha: "
				    		  						+ "<b style='color:red;'>"+linhaAtual+"</b>"
				    		  						+ "</html>", "Detectado overflow!", JOptionPane.ERROR_MESSAGE);
				    		  			}
		    		  					for(int r = 0; r < Janela.dtm.getRowCount(); r++){
					    		  			if(Janela.dtm.getValueAt(r, 0).equals(ArraysLists.regEncontrados.get(1).toString())){
					    		  				valorReg = Janela.dtm.getValueAt(r, 2).toString();
					    		  			}
					    		  		}
		    		  					Memoria.AlocarMemoria(lm, Janela.dtmMem);
		    		  					Janela.painelLinguagemMaquina.append(lm+"\n");
		    		  					Janela.dtmExec.addRow(new Object[]{
		    		  							Memoria.BuscarEndereco(lm.substring(0, 8), Janela.dtmMem),
		    		  							"0x"+ConversaoBase.converteBinarioParaHexadecimal(lm),
		    		  							ArraysLists.operadores.get(i)+" $"+ArraysLists.regEncontrados.get(0).getId()+",$"+ArraysLists.regEncontrados.get(1).getId()+","+enderecoOuLabel,
		    		  							linhaAtual+": "+ArraysLists.operadores.get(i)+" "+ArraysLists.regEncontrados.get(0).toString()+","+ArraysLists.regEncontrados.get(1).toString()+","+enderecoOuLabel});
					    		  		boolean b = ArithmeticLogicUnit.slti(Integer.parseInt(valorReg,10), decimal);
			    		  				for(int r = 0; r < Janela.dtm.getRowCount(); r++){
			    		  					if(Janela.dtm.getValueAt(r, 0).equals(ArraysLists.regEncontrados.get(0).toString())){
			    		  						if(b) { Janela.dtm.setValueAt(Integer.parseInt(valorReg,10), r, 2); }
			    		  						else { Janela.dtm.setValueAt(0, r, 2); }
			    		  					}
			    		  				}
		    		  				break;
		    		  				
		    		  				default:
			    		  				JOptionPane.showMessageDialog(
			    		  						null,
			    		  						"<html>"
			    		  						+ "Lamentamos, mas o operador <b style='font-size:9px; color: red;'>"+ArraysLists.operadores.get(i)+"</b>, localizado na linha <b style='font-size:9px; color: red;'>"+linhaAtual+"</b>,<br>"
			    		  						+ " n�o poder� ser executado.<br>"
			    		  						+ "O mesmo ainda n�o foi completamente habilitado.<br>"
			    		  						+ "<i>Tente utiliz�-lo na pr�xima revis�o do sistema.</i>"
			    		  						+ "</html>",
			    		  						"Operador n�o implementado",
			    		  						JOptionPane.ERROR_MESSAGE);
			    		  			break;
		    		  			}
		    		  		break;
		    		  	
		    		  		
		    		  	// -- TIPO J
		    		  	case 1:
		    		  		Matcher matcher4 = endereco.matcher(linha);
							if(matcher4.find()) { enderecoOuLabel = matcher4.group().substring(1); }
							for(int l = 0; l < ArraysLists.arrLabel.size(); l++){
								if(ArraysLists.arrLabel.get(l).equals(enderecoOuLabel)){
									while(enderecoOuLabel.length() < 8){
										enderecoOuLabel += " ";
									}
									Janela.lblOito = enderecoOuLabel;
									System.out.println(Janela.lblOito);
								}
							}
							if(Memoria.memoria.containsValue(Janela.lblOito)){
								end = Integer.parseInt(Memoria.BuscarEndereco(Janela.lblOito, Janela.dtmMem), 10)+16;
								endLbl = ConversaoBase.converteDecimalParaBinario(end);
							}
							while(endLbl.length()<26){
	    		  				endLbl = "0" + endLbl ;
	    		  			}
							String novoEndLbl = ""+end;
							while(novoEndLbl.length() < 6){
								novoEndLbl = "0"+novoEndLbl;
							}
							lm = TipoInstrucao.InstrucaoTipoJ(ArraysLists.operadores.get(i).getValorBits(),endLbl);
							Memoria.AlocarMemoria(lm, Janela.dtmMem);
							Janela.painelLinguagemMaquina.append(lm+"\n");
							Janela.dtmExec.addRow(new Object[]{
		    		  				Memoria.BuscarEndereco(lm.substring(0, 8), Janela.dtmMem),
		    		  				"0x"+ConversaoBase.converteBinarioParaHexadecimal(lm),
		    		  				ArraysLists.operadores.get(i)+" "+novoEndLbl,
		    		  				linhaAtual+": "+ArraysLists.operadores.get(i)+" "+enderecoOuLabel});
		    		  		break;
		    		  		
		    		  		
		    		  	// -- TIPO R
		    		  	case 2:
		    		  		ArraysLists.regEncontrados.get(0).setAtivo(true);
		    		  		Registrador.AtualizarAtividade(Janela.dtm);
		    		  		lm = TipoInstrucao.InstrucaoTipoR(ArraysLists.regEncontrados.get(0).getValorBits(),ArraysLists.regEncontrados.get(1).getValorBits(),ArraysLists.regEncontrados.get(2).getValorBits(),"00000",ArraysLists.operadores.get(i).getValorBits());
		    		  		Memoria.AlocarMemoria(lm, Janela.dtmMem);
		    		  		Janela.painelLinguagemMaquina.append(lm+"\n");
		    		  		Janela.dtmExec.addRow(new Object[]{
		    		  				Memoria.BuscarEndereco(lm.substring(0, 8), Janela.dtmMem),
		    		  				"0x"+ConversaoBase.converteBinarioParaHexadecimal(lm),
		    		  				ArraysLists.operadores.get(i)+" $"+ArraysLists.regEncontrados.get(0).getId()+",$"+ArraysLists.regEncontrados.get(1).getId()+",$"+ArraysLists.regEncontrados.get(2).getId(),
		    		  				linhaAtual+": "+ArraysLists.operadores.get(i)+" "+ArraysLists.regEncontrados.get(0).toString()+","+ArraysLists.regEncontrados.get(1).toString()+","+ArraysLists.regEncontrados.get(2).toString()});

		    		  		String valorReg2 = null;
		    		  		String valorReg3 = null;
		    		  		for(int r = 0; r < Janela.dtm.getRowCount(); r++){
		    		  			if(Janela.dtm.getValueAt(r, 0).equals(ArraysLists.regEncontrados.get(1).toString())){
		    		  				valorReg2 = Janela.dtm.getValueAt(r, 2).toString();
		    		  			}
		    		  			if(Janela.dtm.getValueAt(r, 0).equals(ArraysLists.regEncontrados.get(2).toString())){
		    		  				valorReg3 = Janela.dtm.getValueAt(r, 2).toString();
		    		  			}
		    		  		}
		    		  		switch(ArraysLists.operadores.get(i).getId()){
		    		  			// ADD
		    		  			case 0:
		    		  				regSalvar = ArithmeticLogicUnit.add(Integer.parseInt(valorReg2,10), Integer.parseInt(valorReg3,10));
		    		  				for(int r = 0; r < Janela.dtm.getRowCount(); r++){
		    		  					if(Janela.dtm.getValueAt(r, 0).equals(ArraysLists.regEncontrados.get(0).toString())){
		    		  						Janela.dtm.setValueAt(regSalvar, r, 2);
		    		  					}
		    		  				}
		    		  			break;
		    		  			// SUB
		    		  			case 1:
		    		  				regSalvar = ArithmeticLogicUnit.sub(Integer.parseInt(valorReg2,10), Integer.parseInt(valorReg3,10));
		    		  				for(int r = 0; r < Janela.dtm.getRowCount(); r++){
		    		  					if(Janela.dtm.getValueAt(r, 0).equals(ArraysLists.regEncontrados.get(0).toString())){
		    		  						Janela.dtm.setValueAt(regSalvar, r, 2);
		    		  					}
		    		  				}
		    		  			break;
	    		  				default:
		    		  				JOptionPane.showMessageDialog(
		    		  						null,
		    		  						"<html>"
		    		  						+ "Lamentamos, mas o operador <b style='font-size:9px; color: red;'>"+ArraysLists.operadores.get(i)+"</b>, localizado na linha <b style='font-size:9px; color: red;'>"+linhaAtual+"</b>,<br>"
		    		  						+ " n�o poder� ser executado.<br>"
		    		  						+ "O mesmo ainda n�o foi completamente habilitado.<br>"
		    		  						+ "<i>Tente utiliz�-lo na pr�xima revis�o do sistema.</i>"
		    		  						+ "</html>",
		    		  						"Operador n�o implementado",
		    		  						JOptionPane.ERROR_MESSAGE);
		    		  			break;
		    		  		}
		    		  		break;
		    		  		
		    		  		
		    		  	// -- TIPO I (JUMP/BRANCH)
		    		  	case 3:
		    		  		break;
		    		  		
		    		  		
		    		  	// -- TIPO I (JUMP/BRANCH)
		    		  	case 4:
		    		  		break;
		    		  		
		    		  		
		    		  	// -- TIPO I (JUMP/BRANCH)
		    		  	case 5:
		    		  		Matcher matcher5 = enderecoTipoI.matcher(linha);
							if(matcher5.find()) { enderecoOuLabel = matcher5.group().substring(1); }
							for(int l = 0; l < ArraysLists.arrLabel.size(); l++){
								if(ArraysLists.arrLabel.get(l).equals(enderecoOuLabel)){
									while(enderecoOuLabel.length() < 8){
										enderecoOuLabel += " ";
									}
									Janela.lblOito = enderecoOuLabel;
									System.out.println(Janela.lblOito);
								}
							}

							endLbl = null;
							end = 0;
							if(Memoria.memoria.containsValue(Janela.lblOito)){
								end = Integer.parseInt(Memoria.BuscarEndereco(Janela.lblOito, Janela.dtmMem), 10)+16;
								endLbl = ConversaoBase.converteDecimalParaBinario(end);
							}
							while(endLbl.length()<16){
	    		  				endLbl = "0" + endLbl ;
	    		  			}
							novoEndLbl = ""+end;
							while(novoEndLbl.length() < 6){
								novoEndLbl = "0"+novoEndLbl;
							}
							lm = TipoInstrucao.InstrucaoTipoI(ArraysLists.operadores.get(i).getValorBits(),ArraysLists.regEncontrados.get(0).getValorBits(),ArraysLists.regEncontrados.get(1).getValorBits(),endLbl);
							Memoria.AlocarMemoria(lm, Janela.dtmMem);
							Janela.painelLinguagemMaquina.append(lm+"\n");
							Janela.dtmExec.addRow(new Object[]{
		    		  				Memoria.BuscarEndereco(lm.substring(0, 8), Janela.dtmMem),
		    		  				"0x"+ConversaoBase.converteBinarioParaHexadecimal(lm),
		    		  				ArraysLists.operadores.get(i)+" $"+ArraysLists.regEncontrados.get(0).getId()+",$"+ArraysLists.regEncontrados.get(1).getId()+","+novoEndLbl,
		    		  				linhaAtual+": "+ArraysLists.operadores.get(i)+" "+ArraysLists.regEncontrados.get(0).toString()+","+ArraysLists.regEncontrados.get(1).toString()+","+enderecoOuLabel});
		    		  		break;
		    		  }
		    		  Janela.painelCima.setSelectedComponent(Janela.linguagemMaquina);
		    		  break;
		    	  }
		      }
		    }
		}
	}
	}
}
