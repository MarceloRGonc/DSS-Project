package business.projetos;

import business.familias.ICandidatura;
import java.util.*;

/**
 * Interface da classe Projeto.
 * @author Jorge Caldas, José Cortez, Marcelo Gonçalves, Ricardo Silva
 * @version 28.12.2014
 */
public interface IProjeto {
        /* Gets & Sets */
    public int getNr();
    public void setNr(int nr);
    public String getEstado();
    public void setEstado(String estado);
    public String getObs();
    public void setObs(String obs);
    public float getOrcamento();
    public void setOrcamento(float orcamento);
    public float getCustoFinal();
    public void setCustoFinal(float custoFinal);
    public float getPrestacao();
    public void setPrestacao(float prestacao);
    public GregorianCalendar getDataInicial();
    public void setDataInicial(GregorianCalendar dataInicial);
    public GregorianCalendar getDataFinal();
    public void setDataFinal(GregorianCalendar dataFinal);
    public int getFuncionarioReg();
    public void setFuncionarioReg(int fr);
    public int getFuncionarioEnc();
    public void setFuncionarioEnc(int fr);
    public Set<Integer> getTarefa();
    public void setTarefa(Set<Integer> t);
    public Set<Integer> getVoluntarios();
    public void setVoluntarios(Set<Integer> voluntarios);
    public ICandidatura getCandidatura();
    public void setCandidatura(ICandidatura candidatura);
    public List<Integer> getNRecibo();
    public void setNRecibo(List<Integer> e);

    /* Equals e Clone */
    @Override
    public boolean equals (Object o);   

    public IProjeto clone();
    
    @Override
    public int hashCode();
}
