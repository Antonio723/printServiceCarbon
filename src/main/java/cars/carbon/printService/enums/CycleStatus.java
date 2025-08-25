package cars.carbon.printService.enums;

public enum CycleStatus {
    DUPLICADO,/** Ciclo duplicado*/
    CRIADO,    /**Quand o cilo foi criado e n'ao tem paakcges atribuidos*/
    PENDENTE,    /** Ciclo aguardando para ser iniciado*/
    RODANDO,    /** Ciclo em execução no equipamento*/
    PAUSADO,     /** Ciclo pausado manualmente*/
    COMPLETED,  /** Ciclo completado com sucesso*/
    CANCELADO,  /** Ciclo cancelado antes da conclusão */
    REPASSE,     /** Falha durante a execução do ciclo*/
    APPROVADO   /** Ciclo aprovado após análise de qualidade*/
}
