import {
  GridContainer,
  Grid,
  Button,
  MediaBlockBody,
  SocialLinks,
  Address,
  Footer,
  FooterNav,
  Logo,
  SocialLink,
  IconList,
} from "@trussworks/react-uswds";
import skillstorm from "../assets/newlogo.png";
import { useTranslation } from "react-i18next";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faCreditCard,
  faChartLine,
  faPiggyBank,
  faBalanceScale,
} from "@fortawesome/free-solid-svg-icons";
import { useDispatch } from "react-redux";
import { useEffect, useRef, useState } from "react";
import { login } from "../app/features/userSlice";
import { useNavigate } from "react-router-dom";
import {
  Account,
  useCreateAccountsMutation,
  useGetAccountsByEmailQuery,
  useUpdateAccountsMutation,
} from "../app/api/accountApi";
import { Bank } from "../app/api/accountApi";
import landingImgEnglish from "../assets/landingImg_english.png";

export default function Landing() {
  const { t, i18n } = useTranslation();
  const navigate = useNavigate();
  const [addAccountMutation, mutationResult] = useCreateAccountsMutation();
  let email = "";
  const dispatch = useDispatch();

  useEffect(() => {
    (async () => {
      await fetch("https://api.aws-tfbd.com/auth/getInfo", {
        credentials: "include",
      })
        .then((res) => res.json())
        .then((data) => (email = data.email));
      dispatch(login(email));
      const newAccount = {
        email: email,
        bankAccounts: [],
        creditCards: [],
        loans: [],
      };
      addAccountMutation(newAccount);

      navigate("/");
    })();
  }, []);

  const handleSignIn = () => {
    window.location.replace("https://api.aws-tfbd.com/auth/login");
    //window.location.replace("http://localhost:8125/auth/login");
  };

  return (
    <>
      <div
        style={{
          maxHeight: "550px",
          width: "100%",
          overflow: "hidden",
          position: "relative",
        }}
      >
        <div style={{ display: "inline-block", position: "relative" }}>
        <img src={landingImgEnglish} style={{ height: "auto", width: "100%" }}/>
        </div>
      </div>
      <div style={{ marginTop: "32px", textAlign: "center" }}>
        <GridContainer>
          <Button
            type="button"
            onClick={handleSignIn}
            style={{
              backgroundColor: "#04c585",
              justifyContent: "center",

              borderRadius: "10px",
              padding: "1em 3em",
            }}
          >
            {t("Landing.Get Started")}
          </Button>
        </GridContainer>
      </div>
      <section
        className="usa-graphic-list usa-section usa-section"
        style={{ backgroundColor: "#e0f7f6" }}
      >
        <GridContainer>
          <Grid row gap className="usa-graphic-list__row">
            <Grid tablet={{ col: true }} className="usa-media-block">
              <IconList className="usa-icon-list--size-lg">
                <FontAwesomeIcon
                  icon={faCreditCard}
                  size="2x"
                  color="#00BFA5"
                  style={{ marginRight: "2.5rem" }}
                />
              </IconList>
              <MediaBlockBody>
                <h2 className="usa-graphic-list__heading ">
                  {t("Landing.Service 1 Header")}
                </h2>
                <p>{t("Landing.Service 1 Body")}</p>
              </MediaBlockBody>
            </Grid>
            <Grid tablet={{ col: true }} className="usa-media-block">
              <IconList className="usa-icon-list--size-lg">
                <FontAwesomeIcon
                  icon={faBalanceScale}
                  size="2x"
                  color="#00BFA5"
                  style={{ marginRight: "2.5rem" }}
                />
              </IconList>
              <MediaBlockBody>
                <h2 className="usa-graphic-list__heading ">
                  {t("Landing.Service 2 Header")}
                </h2>
                <p>{t("Landing.Service 2 Body")}</p>
              </MediaBlockBody>
            </Grid>
          </Grid>
          <Grid row gap className="usa-graphic-list__row">
            <Grid tablet={{ col: true }} className="usa-media-block">
              <IconList className="usa-icon-list--size-lg">
                <FontAwesomeIcon
                  icon={faChartLine}
                  size="2x"
                  color="#00BFA5"
                  style={{ marginRight: "2.5rem" }}
                />
              </IconList>
              <MediaBlockBody>
                <h2 className="usa-graphic-list__heading ">
                  {t("Landing.Service 3 Header")}
                </h2>
                <p>{t("Landing.Service 3 Body")}</p>
              </MediaBlockBody>
            </Grid>
            <Grid tablet={{ col: true }} className="usa-media-block">
              <IconList className="usa-icon-list--size-lg">
                <FontAwesomeIcon
                  icon={faPiggyBank}
                  size="2x"
                  color="#00BFA5"
                  style={{ marginRight: "2.5rem" }}
                />
              </IconList>
              <MediaBlockBody>
                <h2 className="usa-graphic-list__heading ">
                  {t("Landing.Service 4 Header")}
                </h2>
                <p>{t("Landing.Service 4 Body")}</p>
              </MediaBlockBody>
            </Grid>
          </Grid>
        </GridContainer>
      </section>
      <div style={{ backgroundColor: "#e0f7f6" }}>
        <GridContainer className="usa-section">
          <Grid row className="margin-x-neg-205 flex-justify-center">
            <Grid
              col={12}
              mobileLg={{ col: 10 }}
              tablet={{ col: 8 }}
              desktop={{ col: 6 }}
              className="padding-x-205 margin-bottom-4"
              style={{ width: "75%" }}
            >
              <div
                className="padding-x-7 border border-base-lighter text-center"
                style={{
                  paddingLeft: "0rem",
                  paddingRight: "0rem",
                  border: "0px",
                }}
              >
                <h2 id="section-heading-h2">{t("Landing.Marketing Header")}</h2>
                <p style={{ lineHeight: "1.5" }}>
                  {t("Landing.Marketing Body")}
                </p>
              </div>
            </Grid>
          </Grid>
        </GridContainer>
      </div>
      <Footer
        size="medium"
        returnToTop={[]}
        primary={
          <FooterNav
            size="medium"
            links={Array(4).fill(
              <a className="usa-footer__primary-link" href="#"></a>
            )}
          />
        }
        secondary={
          <div className="grid-row grid-gap">
            <Logo
              size="big"
              heading={
                <p className="usa-footer__logo-heading">
                  {t("Landing.A")}&nbsp;&nbsp;
                  {
                    <img
                      className="usa-footer__logo-img"
                      src={skillstorm}
                      alt=""
                      style={{ maxWidth: "10rem", marginBottom: "0px" }}
                    />
                  }
                  &nbsp;&nbsp;{t("Landing.Project")}
                </p>
              }
              image={[]}
            />
            <div className="usa-footer__contact-links mobile-lg:grid-col-6">
              <SocialLinks
                links={[
                  <SocialLink key="facebook" name="Facebook" href="#" />,
                  <SocialLink key="twitter" name="Twitter" href="#" />,
                  <SocialLink key="youtube" name="YouTube" href="#" />,
                  <SocialLink key="facebook" name="Instagram" href="#" />,
                  <SocialLink key="rss" name="RSS" href="#" />,
                ]}
              />
              <h3 className="usa-footer__contact-heading">
                {t("Landing.Contact")}
              </h3>
              <Address
                size="medium"
                items={[
                  <a key="telephone" href="tel:904-438-3440">
                    (904)-438-3440
                  </a>,
                  <a key="email" href="mailto:skillvesting@skillstorm.com">
                    skillvesting@skillstorm.com
                  </a>,
                ]}
              />
            </div>
          </div>
        }
      />
    </>
  );
}
