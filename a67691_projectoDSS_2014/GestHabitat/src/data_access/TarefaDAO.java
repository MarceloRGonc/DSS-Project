package data_access;

import business.projetos.ITarefa;
import business.projetos.TarefaFactory;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementação de um Data Acess Object para gerir instancias da classe Tarefa.
 * @author Jorge Caldas, José Cortez, Marcelo Gonçalves, Ricardo Silva
 * @version 29.12.2014
 */
public class TarefaDAO implements Map<Integer,ITarefa>{
    public Connection conn;
    private MySQLParseTools parseTools;


    public TarefaDAO () throws ConnectionErrorException {
        try {
            this.parseTools = new MySQLParseTools();
            this.conn = (new MySQLManager()).getConnection();
        } catch (SQLException ex) {System.out.println("error_Tarefas_bd");}
    }
    
    @Override
    public int size() {
        try {
            int i = 0;
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("SELECT Id FROM Tarefas");
            while(rs.next()){ i++; }
            return i;
        }
        catch (Exception e) {throw new NullPointerException(e.getMessage());}
    }
    
    @Override
    public boolean isEmpty() {
        try {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("SELECT Id FROM Tarefas");
            return !rs.next();
        }
        catch (Exception e) {throw new NullPointerException(e.getMessage());}
    }
    
    @Override
    public boolean containsKey(Object key) throws NullPointerException {
        try {
            Statement stm = conn.createStatement();
            String sql = "SELECT Designacao FROM Tarefas WHERE Id='"+(int)key+"'";
            ResultSet rs = stm.executeQuery(sql);
            return rs.next();
        }
        catch (Exception e) {throw new NullPointerException(e.getMessage());}
    }
    
    @Override
    public boolean containsValue(Object value) {
        try {
            if(value instanceof ITarefa) {
                ITarefa v = (ITarefa)value;
                ITarefa isv = this.get(v.getId());
                if(isv!=null){
                    if(v.equals(isv)) return true;
                }
                return false;
            } else return false;
        }
        catch (Exception e) {throw new NullPointerException(e.getMessage());}
    }
    
    @Override
    public ITarefa get(Object key) {
        try {
            ITarefa mt = null;
            Statement stm = conn.createStatement();
            String sql = "SELECT * FROM Tarefas WHERE Id='"+(int)key+"'";
            ResultSet rs = stm.executeQuery(sql);
            if (rs.next()){ 
                mt = new TarefaFactory().createTarefa();
                mt.setId(rs.getInt(1));
                mt.setDesig(rs.getString(2));
                mt.setDesc(rs.getString(3));
                mt.setDataInicioT(parseTools.parseSQLDate(rs.getString(4)));
                mt.setDataFinalT(parseTools.parseSQLDate(rs.getString(5)));
                
                HashMap<Integer, Integer> mgasto = new HashMap<>();
                sql = "Select Material, QuantidadeGasta From TarefasMaterial AS TM Where TM.Tarefa='"+mt.getId()+"'";
                rs = stm.executeQuery(sql);
                while(rs.next())
                    mgasto.put(rs.getInt(1),rs.getInt(2));
                mt.setMaterial(mgasto);
            }
            return mt;
        }
        catch (Exception e) {throw new NullPointerException(e.getMessage());}
    }
    
    @Override
    public ITarefa put(Integer key, ITarefa value) {
        try {
            Statement stm = conn.createStatement();
            stm.executeUpdate("DELETE FROM Tarefas WHERE Id='"+key+"'");
            int i  = stm.executeUpdate(insert(key,value));
            ITarefa mt = new TarefaFactory().createTarefa();
            mt.setId(value.getId());
            mt.setDesig(value.getDesig());
            mt.setDesc(value.getDesc());
            mt.setDataInicioT(value.getDataInicioT());
            mt.setDataFinalT(value.getDataFinalT());
            return mt;
        }
        catch (Exception e) {throw new NullPointerException(e.getMessage());}
    }

    private String insert(Integer key, ITarefa value){
        String dataI = parseTools.parseCalendar(value.getDataInicioT());
        String dataF = parseTools.parseCalendar(value.getDataFinalT());
        
        ArrayList<Object> valores = new ArrayList<>();
        valores.add(key);
        valores.add(value.getDesig());
        valores.add(value.getDesc());
        valores.add(dataI);
        valores.add(dataF);
              
        String sql = this.parseTools.createInsert("Tarefas", valores);
        return sql;
    }
    
    @Override
    public ITarefa remove(Object key) {
        try {
            ITarefa mt = this.get(key);
            Statement stm = conn.createStatement();
            stm.executeUpdate("DELETE FROM Tarefas WHERE Id='"+key+"'");
            return mt;
        }
        catch (Exception e) {throw new NullPointerException(e.getMessage());}
    }
    
    @Override
    public Set<Integer> keySet() {
        try {
            Set<Integer> set = new HashSet<>();
            int n = this.generateTarefaKey();
            for(int i=0; i<n; i++){
                if(this.containsKey(i)){
                    set.add(i);
                }
            }
            return set;
        }
        catch (Exception e) {throw new NullPointerException(e.getMessage());}
    }
    
    @Override
    public Collection<ITarefa> values() {
        try {
            Set<ITarefa> set = new HashSet<>();
            Set<Integer> keys = new HashSet<>(this.keySet());
            for(Integer key : keys)
                set.add(this.get(key));
            return set;
        }
        catch (Exception e) {throw new NullPointerException(e.getMessage());}
    }
   
    @Override
    public void clear () {
        try {
            Statement stm = conn.createStatement();
            stm.executeUpdate("DELETE FROM Tarefas WHERE Tarefas.Id>=0;");
        }
        catch (Exception e) {throw new NullPointerException(e.getMessage());}
    }
    
    @Override
    public Set<Entry<Integer,ITarefa>> entrySet() {
        try {
            Set<Entry<Integer,ITarefa>> set;
            Set<Integer> keys = new HashSet<>(this.keySet());            
            
            HashMap<Integer,ITarefa> map = new HashMap<>();
            for(Integer key : keys){
                if(this.containsKey(key)){
                    map.put(key,this.get(key));
                }
            }
            return map.entrySet();
        }
        catch (Exception e) {throw new NullPointerException(e.getMessage());}
    }
    
    @Override
    public boolean equals(Object o){
        if(this==o) return true;
        
        else if(this.getClass() != o.getClass()) return false;
        
        else{
            TarefaDAO mdao = (TarefaDAO) o;
            
            for(ITarefa m : this.values()){
                if(!mdao.containsKey(m.getId())) return false;
                else{
                    if(!m.equals(mdao.get(m.getId()))) return false;
                }
            }
            return true;
        }
    }
    
    @Override
    public void putAll(Map<? extends Integer, ? extends ITarefa> m) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int hashCode() {
        int hash = Arrays.hashCode(new Object[]{conn, parseTools});
        for(ITarefa t : this.values())
            hash+=t.hashCode();
        return hash;
    }
    
    /**
     * Procura a maior chave de projeto existente na base de dados e retorna
     * esse valor incrementado em uma unidade
     * @return Chave que identificará univocamente no sistema um tarefa. 
     */
    public int generateTarefaKey(){
       try {
            if(!this.isEmpty()){
                Statement stm = conn.createStatement();
                ResultSet rs = stm.executeQuery("SELECT MAX(Id) FROM Tarefas;");
                int result = 0;
                if (rs.next()) {      
                    result = rs.getInt(1);  
                } 
                return result + 1;
            } else return 1;
        }
        catch (Exception e) {throw new NullPointerException(e.getMessage());} 
    }
    
    /**
     * Fechar a ligação à base de dados.
     */
    public void close(){
        try {
            this.conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(VoluntarioDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args) throws ConnectionErrorException, Exception {
        TarefaDAO tDAO = new TarefaDAO();
        tDAO.clear();
        
        List<ITarefa> p = new ArrayList<>();
        
        for(int i=0; i<10; i++){
            ITarefa mt = new TarefaFactory().createTarefa();
            mt.setId(tDAO.generateTarefaKey());
            mt.setDataInicioT(new GregorianCalendar(2012,05,i+2));
            mt.setDataFinalT(new GregorianCalendar(2014,05,i+2));
            mt.setDesig("Projectar colunas da casa");
            mt.setDesc("Criar as colunas que irão servir de suporte à casa");
            tDAO.put(mt.getId(), mt);
        }   
        
        System.out.println(tDAO.isEmpty());
        System.out.println(tDAO.containsKey(4));
        tDAO.remove(4);
        System.out.println(tDAO.containsKey(4));
        System.out.println(tDAO.size());

    }
}
