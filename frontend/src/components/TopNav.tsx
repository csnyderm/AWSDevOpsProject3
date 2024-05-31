import {
  Button,
  ExtendedNav,
  Header,
  NavMenuButton,
} from "@trussworks/react-uswds";
import { useState } from "react";
import { useTranslation } from "react-i18next";
import { login, logout, selectUserEmail } from "../app/features/userSlice";
import { useDispatch, useSelector } from "react-redux";
import "../styles/Planner.css";
import SkillvestLogo from "../assets/Skillvest_logo.png";
export default function TopNav() {
  const { t, i18n } = useTranslation();
  const dispatch = useDispatch();
  const onClickLanguageChange = (lng: string) => {
    i18n.changeLanguage(lng); // change the language
  };
  const auth = useSelector((state: any) => state.user.isLoggedIn);

  const [expanded, setExpanded] = useState(false);
  const onClick = (): void => setExpanded((prvExpanded) => !prvExpanded);

  const handleSignIn = () => {
    //dispatch(login(userData.email));
    window.location.replace("https://api.skillstormcloud.com/auth/login"); //needs to change so that it know where to point it to
    //window.location.replace("http://localhost:8125/auth/login");
  };

  const handleSignOut = () => {
    //need to replace this with api call
    dispatch(logout());
    //window.location.replace("http://localhost:8125/auth/logout");
    window.location.replace(
      "https://api.skillstormcloud.com/auth/logout" //needs to change so that it know where to point it to
    );
  };

  const primaryNavItems = [
    <div>
      <br />
      <br />
      <br />
      <br />
    </div>,
  ];

  const secondaryNavItems = auth
    ? [
        <div className="text-base-lightest" style={{ margin: "1em" }}>
          <span
            style={{ cursor: "pointer", color: "black", fontWeight: "bold" }}
            onClick={() => onClickLanguageChange("en")}
          >
            {t("topNav.english")}
          </span>
          {" | "}
          <span
            style={{ cursor: "pointer", color: "black", fontWeight: "bold" }}
            onClick={() => onClickLanguageChange("es")}
          >
            {t("topNav.spanish")}
          </span>
        </div>,

        <Button
          type="button"
          style={{ borderRadius: "10px" }}
          onClick={handleSignOut}
          className="bg-mint"
        >
          {t("topNav.logOut")}
        </Button>,
      ]
    : [
        <div className="text-base-lightest" style={{ margin: "1em" }}>
          <span
            style={{ cursor: "pointer", color: "black", fontWeight: "bold" }}
            onClick={() => onClickLanguageChange("en")}
          >
            {t("topNav.english")}
          </span>
          {" | "}
          <span
            style={{ cursor: "pointer", color: "black", fontWeight: "bold" }}
            onClick={() => onClickLanguageChange("es")}
          >
            {t("topNav.spanish")}
          </span>
        </div>,
        <Button
          type="button"
          style={{ borderRadius: "10px" }}
          onClick={handleSignIn}
          className="bg-mint"
        >
          {t("topNav.signIn")}
        </Button>,
      ];

      return (
        <>
          <div className={`usa-overlay ${expanded ? "is-visible" : ""}`}></div>
          <Header style={{ backgroundColor: "white" }} extended={true}>
            <div
              className="usa-navbar minh-10"
              style={{
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
              }}
            >
              <img src={SkillvestLogo} alt="Skillvesting" />
              <NavMenuButton
                onClick={onClick}
                className="usa-menu-btn bg-mint"
                label="Menu"
                style={{
                  borderRadius: "10px",
                  padding: "10px",
                  marginRight: "10px",
                }}
              />
            </div>
    
            <ExtendedNav
              style={{ backgroundColor: "#e0f7f6" }}
              aria-label="Primary navigation"
              primaryItems={primaryNavItems}
              secondaryItems={secondaryNavItems}
              onToggleMobileNav={onClick}
              mobileExpanded={expanded}
            ></ExtendedNav>
          </Header>
        </>
      );
    }