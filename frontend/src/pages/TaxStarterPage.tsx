import { Button, Grid, GridContainer } from "@trussworks/react-uswds";
import taxImage from "../assets/tax.png";
import { useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";


const TaxStarterPage = (): React.ReactNode => {
  const { t } = useTranslation();

  const navigate = useNavigate();

  const handleNext = () => {
    navigate("/tax-personal-info");
  };

  return (
    <>
      <GridContainer className="padding-top-10 margin-top-5">
        <Grid row={true} className="flex-justify-center">
          <img src={taxImage} alt="Tax Icon" style={{ width: "8em" }} />
        </Grid>
        <Grid row={true} className="flex-justify-center">
          <h1 className="text-center font-heading-2x1">
            {t("taxStarter.getStartedTaxes")}
          </h1>
        </Grid>
        <Grid
          row={true}
          className="text-center flex-justify-center font-heading-2x1"
        >
          <ul className=" text-bold usa-list usa-list--unstyled">
            <li>{t("taxStarter.calculateEstimate")}</li>
            <li>{t("taxStarter.storeInfo")}</li>
            <li>{t("taxStarter.resultsSeconds")}</li>
          </ul>
        </Grid>
        <Grid row={true} className="margin-top-3 flex-justify-center">
          <Button
            type={"button"}
            onClick={handleNext} size="big"
            className="bg-mint"
            style={{borderRadius: "10px"}}
          >
            {t("taxStarter.getStarted")}
          </Button>
        </Grid>
      </GridContainer>
    </>
  );
};

export default TaxStarterPage;
