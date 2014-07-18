package com.ibhh.CurveCraft;

import com.iCo6.system.Accounts;
import com.iConomy.iConomy;
import com.ibhh.CurveCraft.Report.ReportToHost;
import com.ibhh.CurveCraft.logger.LoggerUtility;
import com.ibhh.CurveCraft.logger.LoggerUtility.Level;
import com.nijikokun.register.payment.Method;
import com.nijikokun.register.payment.Method.MethodAccount;
import com.nijikokun.register.payment.Methods;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.scheduler.BukkitScheduler;

public class IConomyHandler {

    private static int iConomyversion = 0;
    private com.iConomy.system.Holdings balance5;
    private Double balance;
    private CurveCraft plugin;
    public static Economy economy = null;

    public IConomyHandler(CurveCraft pl) {
        this.plugin = pl;
        if (setupEconomy().booleanValue() == true) {
            iConomyversion = 2;
            this.plugin.getLoggerUtility().log("hooked into Vault", LoggerUtility.Level.DEBUG);
        }
        this.plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, new Runnable() {
            public void run() {
                IConomyHandler.this.plugin.getLoggerUtility().log("checking MoneyPlugin!", LoggerUtility.Level.DEBUG);
                IConomyHandler.this.iConomyversion();
            }
        }, 0L);
    }

    private static boolean packageExists(String[] packages) {
        try {
            String[] arrayOfString = packages;
            int j = packages.length;
            for (int i = 0; i < j; i++) {
                String pkg = arrayOfString[i];
                Class.forName(pkg);
            }
            return true;
        } catch (Exception localException) {
        }
        return false;
    }

    private Boolean setupEconomy() {
        try {
            RegisteredServiceProvider economyProvider = this.plugin.getServer().getServicesManager().getRegistration(Economy.class);
            if (economyProvider != null) {
                economy = (Economy) economyProvider.getProvider();
            }
        } catch (NoClassDefFoundError e) {
            return Boolean.valueOf(false);
        }
        return Boolean.valueOf(economy != null);
    }

    public int iConomyversion() {
        if (iConomyversion == 0) {
            try {
                if (packageExists(new String[]{"net.milkbowl.vault.economy.Economy"})) {
                    iConomyversion = 2;
                    this.plugin.getLoggerUtility().log("hooked into Vault", LoggerUtility.Level.DEBUG);
                } else if (packageExists(new String[]{"com.nijikokun.register.payment.Methods"})) {
                    iConomyversion = 1;
                    this.plugin.getLoggerUtility().log("hooked into Register", LoggerUtility.Level.DEBUG);
                } else if (packageExists(new String[]{"com.iConomy.iConomy", "com.iConomy.system.Account", "com.iConomy.system.Holdings"})) {
                    iConomyversion = 5;
                    this.plugin.getLoggerUtility().log("hooked into iConomy5", LoggerUtility.Level.DEBUG);
                } else if (packageExists(new String[]{"com.iCo6.system.Accounts"})) {
                    iConomyversion = 6;
                    this.plugin.getLoggerUtility().log("hooked into iConomy6", LoggerUtility.Level.DEBUG);
                } else {
                    this.plugin.getLoggerUtility().log("cant hook into iConomy5, iConomy6, Vault or Register. Downloading Vault!", LoggerUtility.Level.ERROR);
                    this.plugin.getLoggerUtility().log(" ************ Please download and configure Vault!!!!! **********", LoggerUtility.Level.ERROR);
                }
            } catch (Exception E) {
                E.printStackTrace();
                this.plugin.getReportHandler().report(3334, "Error on searching EconomyPlugin", E.getMessage(), "iConomyHandler", E);
                iConomyversion = 0;
            }
            return iConomyversion;
        }
        return 2;
    }

    public double getBalance(Player player) {
        String name = player.getName();
        return getBalance(name);
    }

    public double getBalance(String name) {
        if (iConomyversion == 5) {
            try {
                this.balance5 = getAccount5(name).getHoldings();
            } catch (Exception E) {
                this.plugin.getLoggerUtility().log("No Account! Please report it to an admin!", LoggerUtility.Level.ERROR);
                E.printStackTrace();
                this.balance5 = null;
                return this.balance.doubleValue();
            }
            try {
                this.balance = Double.valueOf(this.balance5.balance());
            } catch (Exception E) {
                this.plugin.getLoggerUtility().log("No Account! Please report it to an admin!", LoggerUtility.Level.ERROR);
                E.printStackTrace();
                this.balance5 = null;
                return this.balance.doubleValue();
            }
            this.balance = Double.valueOf(this.balance5.balance());
            return this.balance.doubleValue();
        }
        if (iConomyversion == 6) {
            try {
                this.balance = new Accounts().get(name).getHoldings().getBalance();
            } catch (Exception e) {
                this.plugin.getLoggerUtility().log("No Account! Please report it to an admin!", LoggerUtility.Level.ERROR);
                e.printStackTrace();
                this.balance = null;
                return this.balance.doubleValue();
            }
            return this.balance.doubleValue();
        }
        if (iConomyversion == 1) {
            try {
                this.balance = Double.valueOf(Methods.getMethod().getAccount(name).balance());
            } catch (Exception e) {
                this.plugin.getLoggerUtility().log("No Account! Please report it to an admin!", LoggerUtility.Level.ERROR);
                e.printStackTrace();
                this.balance = null;
                return this.balance.doubleValue();
            }
            return this.balance.doubleValue();
        }
        if (iConomyversion == 2) {
            this.balance = Double.valueOf(economy.getBalance(name));
            return this.balance.doubleValue();
        }
        return 0.0D;
    }

    private com.iConomy.system.Account getAccount5(String name) {
        return iConomy.getAccount(name);
    }

    public void substract(double amountsubstract, String name) {
        if (iConomyversion == 5) {
            try {
                getAccount5(name).getHoldings().subtract(amountsubstract);
            } catch (Exception e) {
                this.plugin.getLoggerUtility().log("Cant substract money! Does account exist?", LoggerUtility.Level.ERROR);
                e.printStackTrace();
            }
        } else if (iConomyversion == 6) {
            try {
                com.iCo6.system.Account account = new Accounts().get(name);
                account.getHoldings().subtract(amountsubstract);
            } catch (Exception e) {
                this.plugin.getLoggerUtility().log("Cant substract money! Does account exist?", LoggerUtility.Level.ERROR);
                e.printStackTrace();
            }
        } else if (iConomyversion == 1) {
            try {
                Methods.getMethod().getAccount(name).subtract(amountsubstract);
            } catch (Exception e) {
                this.plugin.getLoggerUtility().log("Cant substract money! Does account exist?", LoggerUtility.Level.ERROR);
                e.printStackTrace();
            }
        } else if (iConomyversion == 2) {
            try {
                economy.withdrawPlayer(name, amountsubstract);
            } catch (Exception e) {
                this.plugin.getLoggerUtility().log("Cant substract money! Does account exist?", LoggerUtility.Level.ERROR);
                e.printStackTrace();
            }
        }
    }

    public void substract(double amountsubstract, Player player) {
        String name = player.getName();
        substract(amountsubstract, name);
    }

    public void addmoney(double amountadd, String name) {
        if (iConomyversion == 5) {
            try {
                getAccount5(name).getHoldings().add(amountadd);
            } catch (Exception e) {
                this.plugin.getLoggerUtility().log("Cant substract money! Does account exist?", LoggerUtility.Level.ERROR);
                e.printStackTrace();
            }
        } else if (iConomyversion == 6) {
            try {
                com.iCo6.system.Account account = new Accounts().get(name);
                account.getHoldings().add(amountadd);
            } catch (Exception e) {
                this.plugin.getLoggerUtility().log("Cant substract money! Does account exist?", LoggerUtility.Level.ERROR);
                e.printStackTrace();
            }
        } else if (iConomyversion == 1) {
            try {
                Methods.getMethod().getAccount(name).add(amountadd);
            } catch (Exception e) {
                this.plugin.getLoggerUtility().log("Cant substract money! Does account exist?", LoggerUtility.Level.ERROR);
                e.printStackTrace();
            }
        } else if (iConomyversion == 2) {
            try {
                economy.depositPlayer(name, amountadd);
            } catch (Exception e) {
                this.plugin.getLoggerUtility().log("Cant substract money! Does account exist?", LoggerUtility.Level.ERROR);
                e.printStackTrace();
            }
        }
    }

    public void addmoney(double amountadd, Player player) {
        String name = player.getName();
        addmoney(amountadd, name);
    }
}
