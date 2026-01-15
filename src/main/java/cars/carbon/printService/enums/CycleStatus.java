package cars.carbon.printService.enums;

public enum CycleStatus {
    DUPLICADO,/** Ciclo duplicado*/
    CRIADO,    /**Quand o cilo foi criado e n'ao tem paakcges atribuidos*/
    PENDENTE,    /** Ciclo aguardando para ser iniciado*/
    EM_ANDAMENTO,    /** Ciclo em execução no equipamento*/
    PAUSADO,     /** Ciclo pausado manualmente*/
    CANCELADO,  /** Ciclo cancelado antes da conclusão */
    REPASSE,     /** Falha durante a execução do ciclo*/
    FINALIZADO   /** Ciclo CONCLUIDO após análise de qualidade*/
}
