import { Button, GridContainer, StepIndicator, StepIndicatorStep } from "@trussworks/react-uswds";
import { useTranslation } from "react-i18next";
import PieChartTaxOverview from "../components/PieChartTaxOverview";
import TaxOverviewForm from "../components/tax/forms/TaxOverviewForm";
import { useDeleteReturnByEmailMutation, useFindOneQuery, useUpdateTaxReturnMutation } from "../app/api/taxApi";
import { useSelector } from "react-redux/es/hooks/useSelector";
import { selectUserEmail } from "../app/features/userSlice";
import { useNavigate } from "react-router-dom";

export default function TaxOverview() {

    const { t } = useTranslation();
    const navigate = useNavigate();

    const [deleteTaxReturn] = useDeleteReturnByEmailMutation();

    const userEmail = useSelector(selectUserEmail);
    //const userEmail: string = "testingNewModel@gmail.com"; // temporary
    const { data } = useFindOneQuery(userEmail);
    

    const refundOrOwedAmount =  (data?.totalFedOwed || 0) + (data?.totalStateOwed || 0)

    // Calculate refund or owed amount absolute value
    const refundOrOwedAmountPositive = Math.abs(refundOrOwedAmount).toFixed(2);

    // Determine the label for the refundOrOwedAmount
    const refundOrOwedLabel = refundOrOwedAmount >= 0 ? t("taxOverview.owed") : t("taxOverview.refunded");

    // Wipe current tax return and start over
    const handleStartOver = () => {
        deleteTaxReturn(userEmail);
        navigate('/tax-starter');
    }

    // Wipe current tax return and start over
    const handleReturnHome = () => {
        navigate('/');
    }

    return (
        <>
            <GridContainer>
                <StepIndicator headingLevel="h4" className="bg-info-lighter" >
                    <StepIndicatorStep label={t("stepIndicator.personalInfo")} status="complete" />
                    <StepIndicatorStep label={t("stepIndicator.income")} status="complete" />
                    <StepIndicatorStep label={t("stepIndicator.deductionsCredits")} status="complete" />
                    <StepIndicatorStep label={t("stepIndicator.taxOverview")} status="current" />
                </StepIndicator>
            </GridContainer>
    
            <GridContainer>
                <h1>{t("taxOverview.yourTotal") + " "} {refundOrOwedLabel} {t("taxOverview.is" + ": $")} <span className="text-mint">{refundOrOwedAmountPositive}</span></h1>    
                <PieChartTaxOverview />
                <TaxOverviewForm />
            </GridContainer>

            <GridContainer className="">
                <div className="display-flex">
                    <Button className="margin-top-3 usa-button usa-button--outline text-mint" type="button" style={{borderRadius: "10px"}} 
                            onClick={handleStartOver}>{"< " + t("taxOverview.beginNew")}
                    </Button>
                    <Button className="bg-mint margin-top-3" type="button" onClick={handleReturnHome} style={{borderRadius: "10px"}}>
                        {t("stepIndicator.returnHome")}
                    </Button>
                </div>
            </GridContainer>
            <br/><br/>
        </>
    )
}