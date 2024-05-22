package com.skillstorm.backendtaxes.services;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skillstorm.backendtaxes.customExceptions.InvalidTaxReturnDataException;
import com.skillstorm.backendtaxes.customExceptions.TaxReturnWithEmailDoesNotExistException;
import com.skillstorm.backendtaxes.models.Credit;
import com.skillstorm.backendtaxes.models.Deduction;
import com.skillstorm.backendtaxes.models.IncomeSource;
import com.skillstorm.backendtaxes.models.TaxReturn;
import com.skillstorm.backendtaxes.repositories.TaxReturnRepository;

@Service
public class TaxReturnService {
    
    @Autowired
    private TaxReturnRepository repo;

    // Show all returns in the db
    public List<TaxReturn> findAllReturns(){
        return repo.findAll();
    }

    // Show one return by email. If not present nothing is sent back
    public TaxReturn findReturnByEmail(String email){
        Optional<TaxReturn> taxReturn = repo.findByEmail(email);
        if(taxReturn.isPresent()) {
            return taxReturn.get();
        }
        else {
            return null;
        }
    }

    // Show one income source for a specific tax return. If not present nothing is sent back
    public Optional<IncomeSource> findIncomeSource(String email, int ein) {
        Optional<TaxReturn> optionalTaxReturn = repo.findByEmail(email);
    
        if (optionalTaxReturn.isPresent()) {
            TaxReturn taxReturn = optionalTaxReturn.get();
            IncomeSource[] sources = taxReturn.getIncomeSources();
    
            for (IncomeSource source : sources) {
                if (source.getEmpID() == ein) {
                    return Optional.of(source); // Wrap the source in an optional and return
                }
            }
        }
    
        return Optional.empty(); // Return an empty Optional if source not found
    }
    

    // Delete a tax return by passing in the email
    public void deleteReturnByEmail(String email){
        Optional<TaxReturn> returnToDelete = repo.findByEmail(email);
        if(returnToDelete.isPresent()) {
            repo.delete(returnToDelete.get());
        }
        else {
            throw new TaxReturnWithEmailDoesNotExistException("No tax return found for the provided email address.");
        }
    }

    // Updates an income source for a specific tax return
    public IncomeSource saveTaxReturnIncomeSource(String email, IncomeSource source) {
        Optional<TaxReturn> optionalTaxReturn = repo.findByEmail(email);
    
        if (optionalTaxReturn.isPresent()) {
            TaxReturn taxReturn = optionalTaxReturn.get();
            IncomeSource[] sources = taxReturn.getIncomeSources();
    
            for (int i = 0; i < sources.length; i++) {
                IncomeSource oldSource = sources[i];
                if (oldSource.getEmpID() == source.getEmpID()) {
                    sources[i] = source;         // Update the array with the new source
                    saveTaxReturn(taxReturn);    // Save the tax return with the updated income source
                    return source;               // Return the updated source to confirm it was updated    
                }
            }
        }
        return null; // If the tax return OR the income source is not found, return null
    }

    // Deletes an income source for a specific tax return
    public void deleteIncomeSourceByEmailAndEIN(String email, int ein){
        Optional<TaxReturn> optionalTaxReturn = repo.findByEmail(email);
    
        if (optionalTaxReturn.isPresent()) {
            TaxReturn taxReturn = optionalTaxReturn.get();
            IncomeSource[] sources = taxReturn.getIncomeSources();
            IncomeSource[] newSources = new IncomeSource[sources.length - 1]; // create a replacement array with one less index
            for (int i = 0, j = 0; i < sources.length; i++) {
                IncomeSource oldSource = sources[i];
                if (oldSource.getEmpID() == ein) {      // if the element is the element to be deleted, don't add to the new array
                    continue;
                }

                newSources[j++] = sources[i];           // add all other elements to the new array
            }
            taxReturn.setIncomeSources(newSources);     // set the incomeSources of the return to the new array
            saveTaxReturn(taxReturn);                   // save the tax return with the updated array
        }
    }
    

    // Saves a new tax return to the database or updates an old one
    // Every request will recalculate Federal and State tax returns, as well as taxable income
    public TaxReturn saveTaxReturn(TaxReturn taxReturn) {
        if(taxReturn.getTotalIncome() < 0) {
            throw new InvalidTaxReturnDataException("Total income cannot be negative.");
        }

        // Prevent duplicate returns and not require the frontend to store the tax return's id
        Optional<TaxReturn> existingReturn = repo.findByEmail(taxReturn.getEmail());
        if(existingReturn.isPresent()) {
            taxReturn.setId(existingReturn.get().getId());
        }

        double totalWages = 0;
        double totalBelowLineDeductions = 0;
        double totalAboveLineDeductions = 0;
        double totalCredits = 0;
        double totalFedWithheld = 0;
        double taxableIncome = 0;
        double agi = 0;
        HashMap<String, Double> stateTaxTotals = new HashMap<>();
        HashMap<String, Double> stateTaxWithheld = new HashMap<>();

        // loops through every income source to determine total income, total income for each state, total federal taxes, and total taxes for each state
        if(taxReturn.getIncomeSources() != null) {
            for(IncomeSource incomeSource: taxReturn.getIncomeSources()) {
                double income = incomeSource.getIncome();
                double fedWithheld = incomeSource.getFedWithheld();
                totalWages += income;
                totalFedWithheld += fedWithheld;

                //calculating total income for each state
                String stateAbbreviation = incomeSource.getState();
                double currentTaxTotal = stateTaxTotals.getOrDefault(stateAbbreviation, 0.0);
                double currentTaxWithheld = stateTaxWithheld.getOrDefault(stateAbbreviation, 0.0);
                double newTaxTotal = currentTaxTotal + incomeSource.getIncome();
                double newTaxWithheld = currentTaxWithheld + incomeSource.getStateWithheld();
                stateTaxTotals.put(stateAbbreviation, newTaxTotal);
                stateTaxWithheld.put(stateAbbreviation, newTaxWithheld);
            }
        }

        // adds together all below line deductions
        if(taxReturn.getBelowLineDeductions() != null) {
            for(Deduction deduction: taxReturn.getBelowLineDeductions()) {
                double amount = deduction.getDeductionAmount();
                totalBelowLineDeductions += amount;
            }            
        }

        // adds together all above line deductions
        if(taxReturn.getAboveLineDeductions() != null) {
            for(Deduction deduction: taxReturn.getAboveLineDeductions()) {
                double amount = deduction.getDeductionAmount();
                totalAboveLineDeductions += amount;
            }            
        }

        // FEDERAL TAX CALCULATIONS

        // Subtracting above line deductions to get AGI
        taxReturn.setTotalIncome(totalWages);
        agi = totalWages - totalAboveLineDeductions;
        taxReturn.setAgi(agi);

        // Subtracting below line deductions from gross income to get taxable income
        taxReturn.setTotalIncome(totalWages);
        taxableIncome = agi - totalBelowLineDeductions;
        taxReturn.setTaxableIncome(taxableIncome);

        // TAX CREDIT CALCULATIONS
        // Determine credits for child
        double totalChildCredit = determineChildTaxCredit(taxableIncome, taxReturn.getFilingStatus(), taxReturn.getChildDependents());
        Credit childCredit = new Credit("Child", totalChildCredit);
        // Determine credits for other dependents
        double totalOtherCredit = determineOtherDependentTaxCredit(taxableIncome, taxReturn.getFilingStatus(), taxReturn.getOtherDependents());
        Credit otherCredit = new Credit("Other Dependents", totalOtherCredit);
        // Determine credits for AOTC
        double totalAotcCredit = 0;
        if(taxReturn.getEducationalExpenditures() != null){
            for(double claimAmount : taxReturn.getEducationalExpenditures()){
                double amount = determineAOTCTaxCredit(taxableIncome, taxReturn.getFilingStatus(), claimAmount);
                totalAotcCredit += amount;
            }
        }
        Credit aotcCredit = new Credit("AOTC", totalAotcCredit);
        // Store individual credits
        taxReturn.setCredits(new Credit[]{childCredit, otherCredit, aotcCredit});

        // adds together all credits
        if(taxReturn.getCredits() != null) {
            for(Credit credit: taxReturn.getCredits()) {
                double amount = credit.getCreditAmount();
                totalCredits += amount;
            }            
        }

        //Tax Bracket calculations - calculates tax owed based on tax bracket and filing status then subtracts federal tax already withheld
        if (taxableIncome <= 0) {
            taxReturn.setTotalFedOwed(0);
        } else {
            switch(taxReturn.getFilingStatus()) {
                case "S":
                if (taxableIncome <= 11000) {
                    taxReturn.setTotalFedOwed(taxableIncome * .1 - totalFedWithheld);
                } else if (taxableIncome <= 44725) {
                    taxReturn.setTotalFedOwed(((taxableIncome - 11000) * .12) + 1100 - totalFedWithheld);
                } else if (taxableIncome <= 95375) {
                    taxReturn.setTotalFedOwed(((taxableIncome - 44725) * .22) + 5147 - totalFedWithheld);
                } else if (taxableIncome <= 182100) {
                    taxReturn.setTotalFedOwed(((taxableIncome - 95375) * .24) + 16290 - totalFedWithheld);
                } else if (taxableIncome <= 231250) {
                    taxReturn.setTotalFedOwed(((taxableIncome - 182100) * .32) + 37104 - totalFedWithheld);
                } else if (taxableIncome <= 578125) {
                    taxReturn.setTotalFedOwed(((taxableIncome - 231250) * .35) + 52832 - totalFedWithheld);
                } else if (taxableIncome <= Double.POSITIVE_INFINITY) {
                    taxReturn.setTotalFedOwed(((taxableIncome - 578125) * .37) + 174238.25 - totalFedWithheld);
                }
                break;
            case "MJ":
                if (taxableIncome <= 20550) {
                    taxReturn.setTotalFedOwed(taxableIncome * .1 - totalFedWithheld);
                } else if (taxableIncome <= 83550) {
                    taxReturn.setTotalFedOwed(((taxableIncome - 20550) * .12) + 2200 - totalFedWithheld);
                } else if (taxableIncome <= 178150) {
                    taxReturn.setTotalFedOwed(((taxableIncome - 83550) * .22) + 10294 - totalFedWithheld);
                } else if (taxableIncome <= 340100) {
                    taxReturn.setTotalFedOwed(((taxableIncome - 178150) * .24) + 32580 - totalFedWithheld);
                } else if (taxableIncome <= 431900) {
                    taxReturn.setTotalFedOwed(((taxableIncome - 340100) * .32) + 74208 - totalFedWithheld);
                } else if (taxableIncome <= 647850) {
                    taxReturn.setTotalFedOwed(((taxableIncome - 431900) * .35) + 105664 - totalFedWithheld);
                } else if (taxableIncome <= Double.POSITIVE_INFINITY) {
                    taxReturn.setTotalFedOwed(((taxableIncome - 647850) * .37) + 186601.5 - totalFedWithheld);
                }
                break;
            case "MS":
                if (taxableIncome <= 11000) {
                    taxReturn.setTotalFedOwed(taxableIncome * .1 - totalFedWithheld);
                } else if (taxableIncome <= 44725) {
                    taxReturn.setTotalFedOwed(((taxableIncome - 11000) * .12) + 1100 - totalFedWithheld);
                } else if (taxableIncome <= 95375) {
                    taxReturn.setTotalFedOwed(((taxableIncome - 44725) * .22) + 5147 - totalFedWithheld);
                } else if (taxableIncome <= 182100) {
                    taxReturn.setTotalFedOwed(((taxableIncome - 95375) * .24) + 16290 - totalFedWithheld);
                } else if (taxableIncome <= 231250) {
                    taxReturn.setTotalFedOwed(((taxableIncome - 182100) * .32) + 37104 - totalFedWithheld);
                } else if (taxableIncome <= 346875) {
                    taxReturn.setTotalFedOwed(((taxableIncome - 231250) * .35) + 52832 - totalFedWithheld);
                } else if (taxableIncome <= Double.POSITIVE_INFINITY) {
                    taxReturn.setTotalFedOwed(((taxableIncome - 346875) * .37) + 93300.75 - totalFedWithheld);
                }
                break;
            case "H":
                if (taxableIncome <= 15700) {
                    taxReturn.setTotalFedOwed(taxableIncome * .1 - totalFedWithheld);
                } else if (taxableIncome <= 59850) {
                    taxReturn.setTotalFedOwed(((taxableIncome - 15700) * .12) + 1570 - totalFedWithheld);
                } else if (taxableIncome <= 95350) {
                    taxReturn.setTotalFedOwed(((taxableIncome - 59850) * .22) + 6868 - totalFedWithheld);
                } else if (taxableIncome <= 182100) {
                    taxReturn.setTotalFedOwed(((taxableIncome - 95350) * .24) + 14678 - totalFedWithheld);
                } else if (taxableIncome <= 231250) {
                    taxReturn.setTotalFedOwed(((taxableIncome - 182100) * .32) + 35498 - totalFedWithheld);
                } else if (taxableIncome <= 578100) {
                    taxReturn.setTotalFedOwed(((taxableIncome - 231250) * .35) + 51226 - totalFedWithheld);
                } else if (taxableIncome <= Double.POSITIVE_INFINITY) {
                    taxReturn.setTotalFedOwed(((taxableIncome - 578100) * .37) + 172623.5 - totalFedWithheld);
                }
                break;
            default:
                taxReturn.setTotalFedOwed(0);
            }

        }

        // Subtracting credits from how much is owed
        double totalFedOwedBeforeCredits = taxReturn.getTotalFedOwed();
        taxReturn.setTotalFedOwed(totalFedOwedBeforeCredits - totalCredits);




        //STATE TAX CALCULATIONS
        // reset totalStateOwed to 0 in case of a post request since everything will be recalculated
        taxReturn.setTotalStateOwed(0);
        // state tax is dependent on which state  the incomeSource is from (obviously) so there are 50 possiblilities....
        stateTaxTotals.forEach((key,value) -> {
            double numOfDependents = taxReturn.getChildDependents() + taxReturn.getAotcClaims() + taxReturn.getOtherDependents();
            switch (key) {
            //states with no income tax
            case "AK":
            case "FL":
            case "NV":
            case "NH":
            case "SD":
            case "TN":
            case "TX":
            case "WA":
            case "WY":
            // do nothing
            break;
            // All of the remaining states either have their own individual tax brackets, or a flat tax rate
            // stateIncome formula is typically: total State tax - deduction (if any) - personal exemptions (if any) - exemptions for dependents
            // Some states include deductions/exemptions as credits, in which case the formula is included in the tax brackets 
            case "AL":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value - 1500 - 3000 - (numOfDependents * 1000);
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 500) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (stateIncome * .02 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 3000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 500) * .04) + 10 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 3000) * .05) + 110 - stateTaxWithheld.get(key)));
                            }
                        break;
                    case "MJ":
                        stateIncome = value - 3000 - 8500 - (numOfDependents * 1000);
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 1000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (stateIncome * .02 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 6000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 1000) * .04) + 20 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 6000) * .05) + 220 - stateTaxWithheld.get(key)));
                            }
                        break;

                }
                break;
            case "AZ":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value - 13850;
                        if (stateIncome <= 0) {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - (numOfDependents * 100));
                        } else {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + ((stateIncome - (numOfDependents * 100)) * .025) - stateTaxWithheld.get(key));
                        }
                        break;
                    case "MJ":
                        stateIncome = value - 27700 - (numOfDependents * 100);
                        if (stateIncome <= 0) {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - (numOfDependents * 100));
                        } else {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + ((stateIncome - (numOfDependents * 100)) * .025));
                        }
                }
                break;
            case "AR":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value - 2270;
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 29 - (numOfDependents * 29));
                            } else if (stateIncome <= 4300) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 29 - (numOfDependents * 29) + (stateIncome * .02 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 8500) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 29 - (numOfDependents * 29) + (((stateIncome - 4300) * .04) + 86 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 29 - (numOfDependents * 29) + (((stateIncome - 8500) * .049) + 242 - stateTaxWithheld.get(key)));
                            }
                        break;
                    case "MJ":
                        stateIncome = value - 4540;
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 58 - (numOfDependents * 29));
                            } else if (stateIncome <= 4300) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 58 - (numOfDependents * 29) + (stateIncome * .02 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 8500) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 58 - (numOfDependents * 29) + (((stateIncome - 4300) * .04) + 86 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 58 - (numOfDependents * 29) + (((stateIncome - 8500) * .049) + 242 - stateTaxWithheld.get(key)));
                            }
                        break;

                }
                break;
            case "CA":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value - 5202;
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 140 - (numOfDependents * 433));
                            } else if (stateIncome <= 10099) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 140 - (numOfDependents * 433) + (stateIncome * .01 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 23942) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 140 - (numOfDependents * 433) + (((stateIncome - 10099) * .02) + 100.99 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 37788) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 140 - (numOfDependents * 433) + (((stateIncome - 23942) * .04) + 377.85 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 52455) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 140 - (numOfDependents * 433) + (((stateIncome - 37788) * .06) + 931.69 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 66295) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 140 - (numOfDependents * 433) + (((stateIncome - 52455) * .08) + 1811.71 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 338639) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 140 - (numOfDependents * 433) + (((stateIncome - 66295) * .093) + 2918.91 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 406364) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 140 - (numOfDependents * 433) + (((stateIncome - 338639) * .103) + 28246.90 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 677275) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 140 - (numOfDependents * 433) + (((stateIncome - 406364) * .113) + 35222.58 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 1000000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 140 - (numOfDependents * 433) + (((stateIncome - 677275) * .123) + 65835.52 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 140 - (numOfDependents * 433) + (((stateIncome - 1000000) * .133) + 105530.70 - stateTaxWithheld.get(key)));
                            }
                        break;
                    case "MJ":
                        stateIncome = value - 10404;
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 280 - (numOfDependents * 433));
                            } else if (stateIncome <= 20198) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 280 - (numOfDependents * 433) + (stateIncome * .01 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 47884) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 280 - (numOfDependents * 433) + (((stateIncome - 20198) * .02) + 201.98 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 75576) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 280 - (numOfDependents * 433) + (((stateIncome - 47884) * .04) + 757.70 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 104910) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 280 - (numOfDependents * 433) + (((stateIncome - 75576) * .06) + 1882.58 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 132590) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 280 - (numOfDependents * 433) + (((stateIncome - 104910) * .08) + 3655.94 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 677278) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 280 - (numOfDependents * 433) + (((stateIncome - 132590) * .093) + 5878.34 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 812728) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 280 - (numOfDependents * 433) + (((stateIncome - 677278) * .103) + 58030.78 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 1000000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 280 - (numOfDependents * 433) + (((stateIncome - 812728) * .113) + 71981.39 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 1354550) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 280 - (numOfDependents * 433) + (((stateIncome - 1000000) * .123) + 92176.17 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 280 - (numOfDependents * 433) + (((stateIncome - 1354550) * .133) + 139876.67 - stateTaxWithheld.get(key)));
                            }
                        break;

                }
                break;
            case "CO":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value - 13850;
                        if (stateIncome <= 0) {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                        } else {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + ((stateIncome) * .025));
                        }
                        break;
                    case "MJ":
                        stateIncome = value - 27700;
                        if (stateIncome <= 0) {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                        } else {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + ((stateIncome) * .025));
                        }
                }
                break;
            case "CT":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value - 15000;
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 10000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (stateIncome * .03 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 50000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 10000) * .05) + 500 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 100000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 50000) * .055) + 3250 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 200000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 100000) * .06) + 6250 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 250000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 200000) * .065) + 12750 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 500000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 250000) * .069) + 16200 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 500000) * .0699) + 33675 - stateTaxWithheld.get(key)));
                            }
                        break;
                    case "MJ":
                        stateIncome = value - 24000;
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 20000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (stateIncome * .03 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 100000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 20000) * .05) + 1000 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 200000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 100000) * .055) + 6500 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 400000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 200000) * .06) + 12500 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 500000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 400000) * .065) + 25500 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 1000000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 500000) * .069) + 32400 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 1000000) * .0699) + 67350 - stateTaxWithheld.get(key)));
                            }
                        break;

                }
                break;
            case "DE":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value - 3250;
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 110 - (numOfDependents * 110));
                            } else if (stateIncome <= 2000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 110 - (numOfDependents * 110) + (stateIncome * 0 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 5000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 110 - (numOfDependents * 110) + (((stateIncome - 2000) * .022) + 44.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 10000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 110 - (numOfDependents * 110) + (((stateIncome - 5000) * .039) + 161.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 20000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 110 - (numOfDependents * 110) + (((stateIncome - 10000) * .048) + 401.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 25000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 110 - (numOfDependents * 110) + (((stateIncome - 20000) * .052) + 921.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 60000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 110 - (numOfDependents * 110) + (((stateIncome - 25000) * .0555) + 1198.50 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 110 - (numOfDependents * 110) + (((stateIncome - 60000) * .066) + 4003.50 - stateTaxWithheld.get(key)));
                            }
                        break;
                    case "MJ":
                        stateIncome = value - 6500;
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 220 - (numOfDependents * 110));
                            } else if (stateIncome <= 2000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 220 - (numOfDependents * 110) + (stateIncome * 0 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 5000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 220 - (numOfDependents * 110) + (((stateIncome - 2000) * .022) + 44.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 10000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 220 - (numOfDependents * 110) + (((stateIncome - 5000) * .039) + 161.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 20000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 220 - (numOfDependents * 110) + (((stateIncome - 10000) * .048) + 401.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 25000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 220 - (numOfDependents * 110) + (((stateIncome - 20000) * .052) + 921.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 60000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 220 - (numOfDependents * 110) + (((stateIncome - 25000) * .0555) + 1198.50 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 220 - (numOfDependents * 110) + (((stateIncome - 60000) * .066) + 4003.50 - stateTaxWithheld.get(key)));
                            }
                        break;

                }
                break;
            case "GA":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value - 5400 - 2700 - (numOfDependents * 3000);
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 750) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (stateIncome * .01 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 2250) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 750) * .02) + 15.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 3750) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 2250) * .03) + 60.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 5250) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 3750) * .04) + 120.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 7000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 5250) * .05) + 195.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 7000) * .0575) + 294.56 - stateTaxWithheld.get(key)));
                            }
                        break;
                    case "MJ":
                        stateIncome = value - 7100 - 7400 - (numOfDependents * 3000);
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 1000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (stateIncome * .01 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 3000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 1000) * .02) + 20.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 5000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 3000) * .03) + 80.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 7000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 5000) * .04) + 160.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 10000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 7000) * .05) + 260.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 10000) * .0575) + 432.50 - stateTaxWithheld.get(key)));
                            }
                        break;

                }
                break;
            case "HI":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value - 2200 - 1144 - (numOfDependents * 1144);
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 2400) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (stateIncome * .014 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 4800) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 2400) * .032) + 76.80 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 9600) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 4800) * .055) + 208.80 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 14400) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 9600) * .064) + 516.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 19200) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 14400) * .068) + 1005.60 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 24000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 19200) * .072) + 1351.20 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 36000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 24000) * .076) + 1716 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 48000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 36000) * .079) + 3217.60 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 150000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 48000) * .0825) + 4207.60 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 175000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 150000) * .09) + 13927.60 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 200000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 175000) * .10) + 16427.60 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 200000) * .11) + 19177.60 - stateTaxWithheld.get(key)));
                            }
                        break;
                    case "MJ":
                        stateIncome = value - 4400 - 2288 - (numOfDependents * 1144);
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 4800) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (stateIncome * .014 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 9600) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 4800) * .032) + 153.60 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 19200) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 9600) * .055) + 417.60 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 28800) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 19200) * .064) + 1032.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 38400) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 28800) * .068) + 2011.20 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 48000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 38400) * .072) + 2702.40 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 72000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 48000) * .076) + 3432.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 96000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 72000) * .079) + 6435.20 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 300000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 96000) * .0825) + 8415.20 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 350000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 300000) * .09) + 27855.20 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 400000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 350000) * .10) + 32855.20 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 400000) * .11) + 38355.20 - stateTaxWithheld.get(key)));
                            }
                        break;

                }
                break;
            case "ID":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value - 13850;
                        if (stateIncome <= 0) {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                        } else {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + ((stateIncome) * .058));
                        }
                        break;
                    case "MJ":
                        stateIncome = value - 27700;
                        if (stateIncome <= 0) {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                        } else {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + ((stateIncome) * .058));
                        }
                }
                break;
            case "IL":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value - 2425 - (numOfDependents * 2425);
                        if (stateIncome <= 0) {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                        } else {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + ((stateIncome) * .0495));
                        }
                        break;
                    case "MJ":
                        stateIncome = value - 2850 - (numOfDependents * 2425);
                        if (stateIncome <= 0) {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                        } else {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + ((stateIncome) * .0495));
                        }
                }
                break;
            case "IN":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value - 1000 - (numOfDependents * 1000);
                        if (stateIncome <= 0) {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                        } else {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + ((stateIncome) * .0315));
                        }
                        break;
                    case "MJ":
                        stateIncome = value - 2000 - (numOfDependents * 1000);
                        if (stateIncome <= 0) {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                        } else {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + ((stateIncome) * .0315));
                        }
                }
                break;
            case "IA":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value;
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 40 - (numOfDependents * 40));
                            } else if (stateIncome <= 6000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 40 - (numOfDependents * 40) + (stateIncome * .044 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 30000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 40 - (numOfDependents * 40) + (((stateIncome - 6000) * .0482) + 578.40 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 75000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 40 - (numOfDependents * 40) + (((stateIncome - 30000) * .057) + 1718.40 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 40 - (numOfDependents * 40) + (((stateIncome - 75000) * .06) + 4418.40 - stateTaxWithheld.get(key)));
                            }
                        break;
                    case "MJ":
                        stateIncome = value;
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 80 - (numOfDependents * 40));
                            } else if (stateIncome <= 12000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 80 - (numOfDependents * 40) + (stateIncome * .044 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 60000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 80 - (numOfDependents * 40) + (((stateIncome - 12000) * .0482) + 1156.80 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 150000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 80 - (numOfDependents * 40) + (((stateIncome - 60000) * .057) + 3436.80 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 80 - (numOfDependents * 40) + (((stateIncome - 150000) * .06) + 8836.80 - stateTaxWithheld.get(key)));
                            }
                        break;

                }
                break;
            case "KS":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value - 3500 - 2250 - (numOfDependents * 2250);
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 15000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (stateIncome * .031 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 30000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 15000) * .0525) + 1575.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 30000) * .057) + 3285.00 - stateTaxWithheld.get(key)));
                            }
                        break;
                    case "MJ":
                        stateIncome = value - 8000 - 4500 - (numOfDependents * 2250);
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 30000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (stateIncome * .031 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 60000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 30000) * .0525) + 3150 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 60000) * .057) + 6570 - stateTaxWithheld.get(key)));
                            }
                        break;

                }
                break;
            case "KY":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value - 2770;
                        if (stateIncome <= 0) {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                        } else {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + ((stateIncome) * .045));
                        }
                        break;
                    case "MJ":
                        stateIncome = value - 5540;
                        if (stateIncome <= 0) {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                        } else {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + ((stateIncome) * .045));
                        }
                }
                break;
            case "LA":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value - 4500 - (numOfDependents * 1000);
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 12500) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (stateIncome * .0185 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 50000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 12500) * .035) + 875.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 50000) * .0425) + 4700.00 - stateTaxWithheld.get(key)));
                            }
                        break;
                    case "MJ":
                        stateIncome = value - 9000 - (numOfDependents * 1000);
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 25000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (stateIncome * .0185 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 100000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 25000) * .035) + 1750 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 100000) * .0425) + 9400 - stateTaxWithheld.get(key)));
                            }
                        break;

                }
                break;
            case "ME":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value - 13850 - 4700;
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 24500) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - (numOfDependents * 300) + (stateIncome * .058 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 58050) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - (numOfDependents * 300) + (((stateIncome - 24500) * .675) + 1653.75 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - (numOfDependents * 300) + (((stateIncome - 58050) * .0715) + 3854.60 - stateTaxWithheld.get(key)));
                            }
                        break;
                    case "MJ":
                        stateIncome = value - 27700 - 9400;
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 49050) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - (numOfDependents * 300) + (stateIncome * .058 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 116100) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - (numOfDependents * 300) + (((stateIncome - 49050) * .675) + 3303.38 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - (numOfDependents * 300) + (((stateIncome - 116100) * .0715) + 8858.63 - stateTaxWithheld.get(key)));
                            }
                        break;

                }
                break;
            case "MD":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value - 2400 - 3200 - (numOfDependents * 3200);
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 1000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (stateIncome * .02 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 2000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 1000) * .03) + 30.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 3000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 2000) * .04) + 70.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 100000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 3000) * .0475) + 117.50 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 125000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 100000) * .05) + 4967.50 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 150000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 125000) * .0525) + 6279.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 250000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 150000) * .055) + 7654.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 250000) * .0575) + 19154.00 - stateTaxWithheld.get(key)));
                            }
                        break;
                    case "MJ":
                        stateIncome = value - 4850 - 6400 -(numOfDependents * 3200);
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 1000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (stateIncome * .02 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 2000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 1000) * .03) + 30.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 3000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 2000) * .04) + 70.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 150000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 3000) * .0475) + 117.50 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 175000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 150000) * .05) + 7467.50 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 225000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 175000) * .0525) + 8780.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 300000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 225000) * .055) + 11530.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 300000) * .0575) + 15842.50 - stateTaxWithheld.get(key)));
                            }
                        break;

                }
                break;
            case "MA":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value - 4400 - (numOfDependents * 1000);
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 1000000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (stateIncome * .05 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 1000000) * .09) + 50000 - stateTaxWithheld.get(key)));
                            }
                        break;
                    case "MJ":
                        stateIncome = value - 8800 - (numOfDependents * 1000);
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 1000000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (stateIncome * .05 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 1000000) * .09) + 50000 - stateTaxWithheld.get(key)));
                            }
                        break;

                }
                break;
            case "MI":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value - 5000 - (numOfDependents * 5000);
                        if (stateIncome <= 0) {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                        } else {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + ((stateIncome) * .0425));
                        }
                        break;
                    case "MJ":
                        stateIncome = value - 10000 - (numOfDependents * 5000);
                        if (stateIncome <= 0) {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                        } else {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + ((stateIncome) * .0425));
                        }
                }
                break;
            case "MN":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value - 13825 - (numOfDependents * 4800);
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 30070) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (stateIncome * 0.0535 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 98760) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 30070) * 0.068) + 2044.76 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 183340) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 98760) * .0785) + 8126.34 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 183340) * .0985) + 16524.41 - stateTaxWithheld.get(key)));
                            }
                        break;
                    case "MJ":
                        stateIncome = value - 27650 - (numOfDependents * 4800);
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 43950  ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (stateIncome * .02 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 174610 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 43950 ) * .068) + 2994.60 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 304970 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 174610 ) * .0785) + 13776.94 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 304970 ) * .0985) + 26483.90 - stateTaxWithheld.get(key)));
                            }
                        break;

                }
                break;
            case "MS":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value - 2300 - 6000 - (numOfDependents * 1500);
                        if (stateIncome <= 10000) {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                        } else {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + ((stateIncome) * .05));
                        }
                        break;
                    case "MJ":
                        stateIncome = value - 4600 - 12000 - (numOfDependents * 1500);
                        if (stateIncome <= 10000) {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                        } else {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + ((stateIncome) * .05));
                        }
                }
                break;
            case "MO":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value - 13850;
                        if (stateIncome <= 1121) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 2242 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (stateIncome * .02 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 3363 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 2242 ) * .025) + 22.42 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 4484 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 3363 ) * .03) + 50.47 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 5605 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 4484 ) * .035) + 84.70 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 6726 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 5605 ) * .04) + 124.07 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 7847 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 6726 ) * .045) + 168.91 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 7847 ) * .0495) + 219.52 - stateTaxWithheld.get(key)));
                            }
                        break;
                    case "MJ":
                        stateIncome = value - 27700;
                        if (stateIncome <= 1121) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 2242 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (stateIncome * .02 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 3363 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 2242 ) * .025) + 22.42 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 4484 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 3363 ) * .03) + 50.47 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 5605 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 4484 ) * .035) + 84.70 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 6726 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 5605 ) * .04) + 124.07 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 7847 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 6726 ) * .045) + 168.91 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 7847 ) * .0495) + 219.52 - stateTaxWithheld.get(key)));
                            }
                        break;

                }
                break;
            case "MT":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value - 5540 - 2960  - (numOfDependents * 2960);
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 3600 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (stateIncome * .01 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 6300 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 3600 ) * .02) + 72.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 9700 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 6300 ) * .03) + 153.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 13000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 9700 ) * .04) + 289.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 16800 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 13000 ) * .05) + 454.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 21600 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 16800 ) * .06) + 682.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 21600 ) * .0675) + 1003.75 - stateTaxWithheld.get(key)));
                            }
                        break;
                    case "MJ":
                        stateIncome = value - 11080 - 5920 - (numOfDependents * 2960);
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 3600 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (stateIncome * .01 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 6300 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 3600 ) * .02) + 72.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 9700 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 6300 ) * .03) + 153.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 13000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 9700 ) * .04) + 289.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 16800 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 13000 ) * .05) + 454.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 21600 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 16800 ) * .06) + 682.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 21600 ) * .0675) + 1003.75 - stateTaxWithheld.get(key)));
                            }
                        break;

                }
                break;
            case "NE":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value - 7900;
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 157 - (numOfDependents * 157));
                            } else if (stateIncome <= 3700) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 157 - (numOfDependents * 157) + (stateIncome * .0246 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 22170) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 157 - (numOfDependents * 157) + (((stateIncome - 3700) * .0351) + 129.87 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 35730) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 157 - (numOfDependents * 157) + (((stateIncome - 22170) * .0501) + 1043.88 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 157 - (numOfDependents * 157) + (((stateIncome - 35730) * .0664) + 1947.25 - stateTaxWithheld.get(key)));
                            }
                        break;
                    case "MJ":
                        stateIncome = value - 15800;
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 314 - (numOfDependents * 157));
                            } else if (stateIncome <= 7390) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 314 - (numOfDependents * 157) + (stateIncome * .0246 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 44350) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 314 - (numOfDependents * 157) + (((stateIncome - 7390) * .0351) + 259.35 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 71460) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 314 - (numOfDependents * 157) + (((stateIncome - 44350) * .0501) + 2118.66 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 314 - (numOfDependents * 157) + (((stateIncome - 71460) * .0664) + 3928.67 - stateTaxWithheld.get(key)));
                            }
                        break;

                }
                break;
            case "NJ":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value - 1000 - (numOfDependents * 1500);
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 20000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (stateIncome * .014 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 35000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 20000 ) * .175) + 350.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 40000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 35000 ) * .035) + 875.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 75000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 40000 ) * .05525) + 1151.25 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 500000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 75000 ) * .0637) + 3344.50 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 1000000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 500000 ) * .0897) + 41917.75 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 1000000 ) * .1075) + 95667.75 - stateTaxWithheld.get(key)));
                            }
                        break;
                    case "MJ":
                        stateIncome = value - 2000 - (numOfDependents * 1500);
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 20000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (stateIncome * .014 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 50000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 20000 ) * .175) + 350.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 70000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 50000 ) * .0245) + 962.50 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 80000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 70000 ) * .035) + 1662.50 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 150000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 80000 ) * .05525) + 2214.75 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 500000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 150000 ) * .0637) + 7047.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 1000000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 500000 ) * .0897) + 39199.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 1000000 ) * .1075) + 92949.00 - stateTaxWithheld.get(key)));
                            }
                        break;

                }
                break;
            case "NM":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value - 13850 - (numOfDependents * 4000);
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 5500 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (stateIncome * .017 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 11000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 5500 ) * .032) + 176.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 16000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 11000 ) * .047) + 440.50 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 210000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 16000 ) * .049) + 685.50 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 210000 ) * .059) + 12204.10 - stateTaxWithheld.get(key)));
                            }
                        break;
                    case "MJ":
                        stateIncome = value - 27700 - (numOfDependents * 4000);
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 8000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (stateIncome * .017 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 16000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 8000 ) * .032) + 256.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 24000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 16000 ) * .047) + 632.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 315000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 24000 ) * .049) + 1024.00 - stateTaxWithheld.get(key)));                          
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 315000 ) * .059) + 17881.60 - stateTaxWithheld.get(key)));
                            }
                        break;

                }
                break;
            case "NY":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value - 8000 - (numOfDependents * 1000);
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 8500 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (stateIncome * .04 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 11700 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 8500 ) * .045) + 382.50 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 13900 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 11700 ) * .0525) + 693.75 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 80650 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 13900 ) * .055) + 814.75 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 215400 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 80650 ) * .06) + 4687.75 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 1077550 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 215400 ) * .0685) + 14568.22 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 5000000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 1077550 ) * .0965) + 100727.23 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 25000000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 5000000 ) * .103) + 498372.58 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 25000000 ) * .109) + 2568372.58 - stateTaxWithheld.get(key)));
                            }
                        break;
                    case "MJ":
                        stateIncome = value - 16050 - (numOfDependents * 1000);
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 17150 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (stateIncome * .04 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 23600 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 17150 ) * .045) + 771.75 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 27900 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 23600 ) * .0525) + 1109.06 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 161550 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 27900 ) * .055) + 1345.61 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 323200 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 161550 ) * .06) + 9448.61 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 2155350 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 323200 ) * .0685) + 21200.53 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 5000000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 2155350 ) * .0965) + 211661.09 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 25000000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 5000000 ) * .103) + 512392.73 - stateTaxWithheld.get(key)));                          
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 25000000 ) * .109) + 2557392.73 - stateTaxWithheld.get(key)));
                            }
                        break;

                }
                break;
            case "NC":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value - 12750;
                        if (stateIncome <= 0) {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                        } else {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + ((stateIncome) * .0475));
                        }
                        break;
                    case "MJ":
                        stateIncome = value - 25500;
                        if (stateIncome <= 0) {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                        } else {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + ((stateIncome) * .0475));
                        }
                }
                break;
            case "ND":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value - 13850;
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 41775 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (stateIncome * .011 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 101050 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 41775 ) * .0204) + 852.81 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 210825 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 101050 ) * .0227) + 2135.38 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 458350 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 210825 ) * .0264) + 4865.78 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 458350 ) * .0290) + 12118.35 - stateTaxWithheld.get(key)));
                            }
                        break;
                    case "MJ":
                        stateIncome = value - 27700;
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 69700 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (stateIncome * .011 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 168450 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 69700 ) * .0204) + 1423.88 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 256650 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 168450 ) * .0227) + 3586.80 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 458350 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 256650 ) * .0264) + 5894.39 - stateTaxWithheld.get(key)));                          
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 458350 ) * .0290) + 11873.07 - stateTaxWithheld.get(key)));
                            }
                        break;

                }
                break;
            case "OH":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value - 2400 - (numOfDependents * 2400);
                        if (stateIncome <= 26050) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 46100 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 26050) * .02765) - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 92150 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 46100 ) * .03226) + 545.25 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 115300 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 92150 ) * .03688) + 2037.35 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 115300 ) * .0399) + 4286.75 - stateTaxWithheld.get(key)));
                            }
                        break;
                    case "MJ":
                        stateIncome = value - 4800 - (numOfDependents * 2400);
                        if (stateIncome <= 26050) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 46100 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 26050) * .02765) - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 92150 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 46100 ) * .03226) + 545.25 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 115300 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 92150 ) * .03688) + 2037.35 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 115300 ) * .0399) + 4286.75 - stateTaxWithheld.get(key)));
                            }
                        break;

                }
                break;
            case "OK":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value - 6350 - 1000 - (numOfDependents * 1000);
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 1000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (stateIncome * .0025 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 2500 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 1000 ) * .0075) + 2.50 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 3750 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 2500 ) * .0175) + 13.75 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 4900 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 3750 ) * .0275) + 40.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 7200 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 4900 ) * .0375) + 71.87 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 7200 ) * .0475) + 157.37 - stateTaxWithheld.get(key)));
                            }
                        break;
                    case "MJ":
                        stateIncome = value - 12700 - 2000 - (numOfDependents * 1000);
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 2000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (stateIncome * .0025 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 5000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 2000 ) * .0075) + 5.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 7500 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 5000 ) * .0175) + 27.50 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 9800 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 7500 ) * .0275) + 71.25 - stateTaxWithheld.get(key))); 
                            } else if (stateIncome <= 12200 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 9800 ) * .0375) + 134.43 - stateTaxWithheld.get(key)));                         
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 12200 ) * .0475) + 207.93 - stateTaxWithheld.get(key)));
                            }
                        break;

                }
                break;
            case "OR":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value - 2605;
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 236 - (numOfDependents * 236));
                            } else if (stateIncome <= 4050) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 236 - (numOfDependents * 236) + (stateIncome * .0475 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 10200) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 236 - (numOfDependents * 236) + (((stateIncome - 4050) * .0675) + 192.38 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 125000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 236 - (numOfDependents * 236) + (((stateIncome - 10200) * .0875) + 628.13 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 236 - (numOfDependents * 236) + (((stateIncome - 125000) * .099) + 11200.63 - stateTaxWithheld.get(key)));
                            }
                        break;
                    case "MJ":
                        stateIncome = value - 5210;
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 472 - (numOfDependents * 236));
                            } else if (stateIncome <= 8100) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 472 - (numOfDependents * 236) + (stateIncome * .0475 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 20400) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 472 - (numOfDependents * 236) + (((stateIncome - 8100) * .0675) + 518.70 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 250000) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 472 - (numOfDependents * 236) + (((stateIncome - 20400) * .0875) + 4237.32 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 472 - (numOfDependents * 236) + (((stateIncome - 250000) * .099) + 7857.34 - stateTaxWithheld.get(key)));
                            }
                        break;

                }
                break;
            case "PA":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value;
                        if (stateIncome <= 0) {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                        } else {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + ((stateIncome) * .0307));
                        }
                        break;
                    case "MJ":
                        stateIncome = value;
                        if (stateIncome <= 0) {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                        } else {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + ((stateIncome) * .0307));
                        }
                }
                break;
            case "RI":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value - 10000 - 4700 - (numOfDependents * 4700);
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 68200 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (stateIncome * .0375 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 155050 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 68200 ) * .0475) + 2557.50 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 155050 ) * .0599) + 7297.94 - stateTaxWithheld.get(key)));
                            }
                        break;
                    case "MJ":
                        stateIncome = value - 20050 - 9400 - (numOfDependents * 4700);
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 68200 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (stateIncome * .0375 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 155050 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 68200 ) * .0475) + 2557.50 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 155050 ) * .0599) + 7297.94 - stateTaxWithheld.get(key)));
                            }
                        break;

                }
                break;
            case "SC":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value - 13850 - (numOfDependents * 4430);
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 3200 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (stateIncome * .0 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 16040 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 3200 ) * .03) - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 16040 ) * .065) + 376.20 - stateTaxWithheld.get(key)));
                            }
                        break;
                    case "MJ":
                        stateIncome = value - 27700 - (numOfDependents * 4430);
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 3200 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (stateIncome * .0 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 16040 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 3200 ) * .03) - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 16040 ) * .065) + 376.20 - stateTaxWithheld.get(key)));
                            }
                        break;

                }
                break;
            case "UT":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value;
                        if (stateIncome <= 0) {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                        } else {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 831 - (numOfDependents * 1802)  + ((stateIncome) * .0485));
                        }
                        break;
                    case "MJ":
                        stateIncome = value;
                        if (stateIncome <= 0) {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                        } else {
                            taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() - 1662 - (numOfDependents * 1802)  + ((stateIncome) * .0485));
                        }
                }
                break;
            case "VT":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value - 6500 - 4500 - (numOfDependents * 4500);
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 42150 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + ((stateIncome * .0335) - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 102200 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 42150 ) * .0660) + 1413.23 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 213150 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 102200 ) * .0760) + 5506.42 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 213150 ) * .0875) + 13859.02 - stateTaxWithheld.get(key)));
                            }
                        break;
                    case "MJ":
                        stateIncome = value - 13050 - 9000 - (numOfDependents * 4500);
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 70450 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + ((stateIncome * .0335) - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 170300 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 70450 ) * .0660) + 2363.58 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 259500 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 170300 ) * .0760) + 9321.18 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 259500 ) * .0875) + 16111.14 - stateTaxWithheld.get(key)));
                            }
                        break;

                }
                break;
            case "VA":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value - 8000 - 930 - (numOfDependents * 930);
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 3000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + ((stateIncome * .02) - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 5000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 3000 ) * .03) + 60 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 17000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 5000 ) * .05) + 120 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 17000 ) * .0575) + 720 - stateTaxWithheld.get(key)));
                            }
                        break;
                    case "MJ":
                        stateIncome = value - 16000 - 1860 - (numOfDependents * 930);
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 3000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + ((stateIncome * .02) - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 5000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 3000 ) * .03) + 60 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 17000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 5000 ) * .05) + 120 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 17000 ) * .0575) + 720 - stateTaxWithheld.get(key)));
                            }
                        break;

                }
                break;
            case "WV":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value - 2000 - (numOfDependents * 2000);
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 10000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + ((stateIncome * .03) - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 25000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 10000 ) * .04) + 300.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 40000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 25000 ) * .05) + 900.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 60000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 40000 ) * .06) + 1575.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 60000 ) * .065) + 2775.00 - stateTaxWithheld.get(key)));
                            }
                        break;
                    case "MJ":
                        stateIncome = value - 4000 - (numOfDependents * 2000);
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 10000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + ((stateIncome * .03) - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 25000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 10000 ) * .04) + 300.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 40000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 25000 ) * .05) + 900.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 60000 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 40000 ) * .06) + 1575.00 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 60000 ) * .065) + 2775.00 - stateTaxWithheld.get(key)));
                            }
                        break;

                }
                break;
            case "WI":
                switch (taxReturn.getFilingStatus()) {
                    case "S":
                    case "MS":
                    case "H":
                        double stateIncome = value - 12760 - 700 - (numOfDependents * 700);
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 13810 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + ((stateIncome * .0354) - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 27630 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 13810 ) * .0465) + 489.77 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 304170 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 27630 ) * .053) + 1186.47 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 304170 ) * .0765) + 15871.42 - stateTaxWithheld.get(key)));
                            }
                        break;
                    case "MJ":
                        stateIncome = value - 23620 - 1400 - (numOfDependents * 700);
                        if (stateIncome <= 0) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed());
                            } else if (stateIncome <= 18420 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + ((stateIncome * .0354) - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 36840 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 18420 ) * .0465) + 652.81 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= 405550 ) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 36840 ) * .053) + 1519.15 - stateTaxWithheld.get(key)));
                            } else if (stateIncome <= Double.POSITIVE_INFINITY) {
                                taxReturn.setTotalStateOwed(taxReturn.getTotalStateOwed() + (((stateIncome - 405550 ) * .0765) + 21913.50 - stateTaxWithheld.get(key)));
                            }
                        break;

                }
                break;

        }
        });

        return repo.save(taxReturn);
    }

    // Determining a child tax credit is dependent on the MAGI of the applicant and their filing status
    public double determineChildTaxCredit(double MAGI, String filingStatus, int claims){
        if(filingStatus.equals("MJ")){
            // If MJ and above threshold, no child tax credit
            if(MAGI > 440000){
                    return 0;
            }
            // If within 400,000 and 440,000 then use calculation formula
            else if(MAGI >= 400000){
                return claims * (2000 - (MAGI - 400000) * .05);
            }
            // Below 400,000 no calculation needed, $2K per child
            else{
                return 2000 * claims;
            }
        }
        else{
            // If above threshold, no child tax credit
            if(MAGI > 240000){
                    return 0;
            }
            // If within 200,000 and 240,000 then use calculation formula
            else if(MAGI >= 200000){
                return claims * (2000 - (MAGI - 200000) * .05);
            }
            // Below 200,000 no calculation needed, $2K per child
            else{
                return 2000 * claims;
            }
        }
    }

    // Determining the credit for an other dependent is based of the filler's MAGI and their filing status
    public double determineOtherDependentTaxCredit(double MAGI, String filingStatus, int claims){
        if(filingStatus.equals("MJ")){
            // If MJ and above threshold, no other dependent tax credit
            if(MAGI > 420000){
                    return 0;
            }
            // If within 400,000 and 440,000 then use calculation formula
            else if(MAGI >= 400000){
                return claims * (500 - (MAGI - 400000) * .025);
            }
            // Below 400,000 no calculation needed, $2K per claimant
            else{
                return 500 * claims;
            }
        }
        else{
            // If above threshold, no other dependent tax credit
            if(MAGI > 220000){
                    return 0;
            }
            // If within 200,000 and 240,000 then use calculation formula
            else if(MAGI >= 200000){
                return claims * (500 - (MAGI - 200000) * .025);
            }
            // Below 200,000 no calculation needed, $2K per claimant
            else{
                return 500 * claims;
            }
        }
    }

    // Determining the credit for AOTC is based of the filler's MAGI, their filing status, and the QEE per claim
    public double determineAOTCTaxCredit(double MAGI, String filingStatus, double QEE){
        if(filingStatus.equals("MJ")){
            // If MJ and above threshold, no AOTC tax credit
            if(MAGI > 180000){
                    return 0;
            }
            // If QEE is above 2000, then use formula
            else if(QEE >= 2000){
                return 2000 + .25 * (QEE - 2000);
            }
            // If QEE is below 2000, then receive credit equal to QEE
            else{
                return QEE;
            }
        }
        else{
            // If above threshold, no AOTC tax credit
            if(MAGI > 90000){
                    return 0;
            }
            // If QEE is above 4000, then receive max amount of AOTC
            if(QEE > 4000){
                return 2500;
            }
            // If QEE is above 2000, then use formula
            else if(QEE >= 2000){
                return 2000 + .25 * (QEE - 2000);
            }
            // If QEE is below 2000, then receive credit equal to QEE
            else{
                return QEE;
            }
        }
    }
}
