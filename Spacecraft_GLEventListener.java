import gmaths.*;

import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.*;

public class Spacecraft_GLEventListener implements GLEventListener {

  private static final boolean DISPLAY_SHADERS = false;

  public Spacecraft_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(4f,12f,18f));
  }

  // ***************************************************
  /*
   * METHODS DEFINED BY GLEventListener
   */

  /* Most oif this is my work but i have adapted the tutorials to aid me */

  /* Initialisation */
  public void init(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    gl.glClearDepth(1.0f);
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDepthFunc(GL.GL_LESS);
    gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
    gl.glDisable(GL.GL_CULL_FACE); // default is 'not enabled'
    gl.glCullFace(GL.GL_BACK);// default is 'back', assuming CCW
    gl.glEnable(GL.GL_BLEND);
    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);


    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);

    initialise(gl);
    startTime = getSeconds();

  }

  /* Called to indicate the drawing surface has been moved and/or resized  */
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
    float aspect = (float)width/(float)height;
    camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
  }

  /* Draw */
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    render(gl);
  }

  /* Clean up memory, if necessary */
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    light.dispose(gl);
    floor.dispose(gl);
    skyBox.dispose(gl);
    cube.dispose(gl);
    textures.destroy(gl);
  }


  // ***************************************************
  /* INTERACTION
   *
   *
   */

  public void incXPosition() {
    xPosition += 0.5f;
    if (xPosition>5f) xPosition = 5f;
    updateX();
  }

  public void decXPosition() {
    xPosition -= 0.5f;
    if (xPosition<-5f) xPosition = -5f;
    updateX();
  }

  private void updateX() {
    translateX.setTransform(Mat4Transform.translate(xPosition,0,0));
    translateX.update(); // IMPORTANT - the scene graph has changed

  }


  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */

  // textures
  private TextureLibrary textures;

  private Camera camera;
  private Mat4 perspective;
  private Model floor, cube, box, globe, axis, smllRobot, wall, rWall, ceiling, skyBox, robotStand, robotArm, robotEyes,
          robotFace, robotskin, roombaEyes, robotSpotlight;
  private Light light;
  private SGNode roomWallRoot, roomObjectsRoot, roombaRoot, bigRobot, twoBranchRoot;

  private TransformNode translateX, translateZ, rotateAll, rotateUpper, rotateOnAxis, moveForward, turnLeft,
          reverseRotateAll, armWave_1, rotateOnAxis_2, rotateOnAxis_3, rotateOnAxis_4
          ,rotateOnAxis_5, armWave_2, rotateOnAxis_6;

  // positions movers
  private float xPosition = 1.5f;
  private float zPosition = 6f;
  private float moveForwardZ = 0;
  private float moveForwardX = 0;
  private float rotateAllAngleStart = 25, rotateAllAngle = rotateAllAngleStart;
  private float rotateUpperAngleStart = -60, rotateUpperAngle = rotateUpperAngleStart;
  private float globeRotationAngle = 0 ;
  private float rotateSpeed = -10;
  private float rotateSpeed_2 = -3;
  private float skyrotate = 0;



  private void initialise(GL3 gl) {
    createRandomNumbers();

    textures = new TextureLibrary();
    textures.add(gl, "chequerboard", "assets/textures/Specular_tile.png", GL.GL_RGBA);
    textures.add(gl, "chequerboard2", "assets/textures/diffuse_tile.png", GL.GL_RGBA);

    textures.add(gl, "diffuse", "assets/textures/jade.jpg", GL.GL_RGBA);
    textures.add(gl, "specular", "assets/textures/jade_specular.jpg", GL.GL_RGBA);
    textures.add(gl, "green", "assets/textures/diffuse_leftwallBg.png", GL.GL_RGBA);
    textures.add(gl, "metal", "assets/textures/metal.jpg", GL.GL_RGBA);
    textures.add(gl, "specular_globe", "assets/textures/specular_globe.png", GL.GL_RGBA);
    textures.add(gl, "diffuse_globe", "assets/textures/diffuse_globe.png", GL.GL_RGBA);
    textures.add(gl,"standTex", "assets/textures/cool_diffuse.png", GL.GL_RGBA);
    textures.add(gl,"standTex2", "assets/textures/cool_specular.png", GL.GL_RGBA);
    textures.add(gl,"namediff", "assets/textures/diffuse_reuel.png", GL.GL_RGBA);
    textures.add(gl,"namespec", "assets/textures/specular_reuel.png", GL.GL_RGBA);
    textures.add(gl,"rightWallBg", "assets/textures/diffuse_rightWallBg.png", GL.GL_REPEAT);
    textures.add(gl,"skybox", "assets/textures/diffuse_skyBox.jpg", GL.GL_REPEAT);
    textures.add(gl, "whiteBg", "assets/textures/diffuse_white.png", GL.GL_RGBA);
    textures.add(gl, "eyes", "assets/textures/diffuse_eyes1.png", GL.GL_RGBA);
    textures.add(gl, "face", "assets/textures/diffuse_face.png", GL.GL_RGBA);
    textures.add(gl, "skin", "assets/textures/diffuse_skin.png", GL.GL_RGBA);
    textures.add(gl, "spotlight", "assets/textures/diffuse_spotlight.png", GL.GL_RGBA);
    textures.add(gl, "roombaSkin", "assets/textures/diffuse_roombaSkin.png", GL.GL_RGBA);


    light = new Light(gl);
    light.setCamera(camera);

    floor = makeFloor(gl, textures.get("chequerboard"), textures.get("chequerboard2"));
    skyBox = makeSky(gl, textures.get("skybox"));
    cube = makeCube(gl, textures.get("green"), textures.get("green"));
    smllRobot = makeSmallRobot(gl, textures.get("roombaSkin"));
    box = makeStand(gl, textures.get("standTex"), textures.get("standTex2"));
    globe = makeSphere2(gl, textures.get("diffuse_globe"), textures.get("specular_globe1"));
    axis = makeSphere(gl, textures.get("eyes"));
    wall = makeBackWall(gl, textures.get("namediff"), textures.get("namespec"));
    rWall = makeRightWall(gl, textures.get("rightWallBg"));
    ceiling = makePlane(gl, textures.get("green"));
    robotStand = makeSphere2(gl, textures.get("diffuse"), textures.get("specular"));
    roombaEyes = makeSphere2(gl, textures.get("whiteBg"), textures.get("whiteBg"));
    robotArm = makeSphere2(gl, textures.get("skin"), textures.get("skin"));
    robotEyes = makeSphere2(gl, textures.get("eyes"),textures.get("eyes"));
    robotFace = makeSphere2(gl, textures.get("face"),textures.get("face"));
    robotskin = makeSphere2(gl, textures.get("skin"), textures.get("skin"));
    robotSpotlight = makeSphere2(gl, textures.get("spotlight"), textures.get("spotlight"));



    roomWallRoot = new NameNode("two-branch structure");
    roomObjectsRoot = new NameNode("room objects");
    roombaRoot = new NameNode("Little Robot");
    bigRobot = new NameNode("Big Robot");
    twoBranchRoot = new NameNode("two-branch structure");





    //Room
    SGNode ceiling_0 = makingCeiling(ceiling);
    SGNode rightWall_1 = makeRmWall(rWall);
    SGNode leftWall_1 = makeRmWall2_0(cube);
    SGNode leftWall_2 = makeRmWall2_1(cube);
    SGNode leftWall_3 = makeRmWall2_2(cube);
    SGNode leftWall_4 = makeRmWall2_3(cube);
    SGNode backWall = makeRmWall3(wall);



    // GLobe
    SGNode stand = makeGlobeStand(box);
    SGNode axiss = makeGlobeAxis(axis);
    SGNode globe = makeGlobe(this.globe);
    SGNode skyB = makeSky(skyBox);

    //Robot
    SGNode miniRobot_1 = makeRoomba(smllRobot);
    SGNode rbt2_eye_1 = makeEyes_1(roombaEyes);
    SGNode rbt2_eye_2 = makeEyes_1(roombaEyes);
    SGNode track = makeTrack(roombaEyes);
    SGNode spotLight_p1 = makeSpotLight_1(robotSpotlight);



    //Dancing robot pieces
    SGNode bigRobot_Stand = makeRobotStand(robotskin);
    SGNode robotHead = makeHead(robotFace);
    SGNode baseLeg = makePole(robotskin);
    SGNode upperBaseLeg = makePole(robotskin);
    SGNode midBody = makeMidBody(robotskin);
    SGNode arm1 = makeArm(robotArm);
    SGNode arm2 = makeArm(robotArm);
    SGNode hair = makeHair(robotEyes);
    SGNode hair_2 = makeHair(robotEyes);
    SGNode hole_1 = makeNose(robotArm);
    SGNode hole_2 = makeNose(robotArm);




    TransformNode translateToTop = new TransformNode("translate(0)",
            Mat4Transform.translate(0.15f,1.35f,0));

    TransformNode translateToStand = new TransformNode("translateToBase",
            Mat4Transform.translate(-1.5f,0.0f,-5.25f));

    TransformNode translateToBaseLegTop = new TransformNode("translateToBaseLegTop",
            Mat4Transform.translate(0,0.8f,0));

    TransformNode translateToUpperLeg = new TransformNode("translateToUpperLeg",
            Mat4Transform.translate(0,0.8f,0));

    TransformNode translateToMidBTop = new TransformNode("translateToMidBTop",
            Mat4Transform.translate(0,0.6f,0));

    TransformNode translateLeftHair = new TransformNode("translateLeftHair",
            Mat4Transform.translate(-0.3f,0.4f,0));


    TransformNode translateRightHair = new TransformNode("translateRightHair",
            Mat4Transform.translate(0.3f,0.4f,0));


    TransformNode translateLeftArm = new TransformNode("translateLeftArm",
            Mat4Transform.translate(0.4f,0.4f,0));

    TransformNode translateRightArm = new TransformNode("translateRightArm",
            Mat4Transform.translate(-0.4f,0.4f,0));

    TransformNode translateNose = new TransformNode("translateNose",
            Mat4Transform.translate(-0.3f,0.5f,0.1f));

    TransformNode translateNose_2 = new TransformNode("translateNose",
            Mat4Transform.translate(0.3f,0.5f,0.1f));

    TransformNode translateToFloor = new TransformNode("translateToFloor",
            Mat4Transform.translate(0,0f,0));

    TransformNode translateToLine = new TransformNode("translateToLine",
            Mat4Transform.translate(3.5f,0f,0));

    TransformNode translateRob2toLine = new TransformNode("translateRob2toLine",
            Mat4Transform.translate(0f,0f,0));

    TransformNode translateEyeToR = new TransformNode("translateEyes",
            Mat4Transform.translate(0.2f,0f,-0.5f));




    TransformNode translateEyeToL = new TransformNode("translateEyes",
            Mat4Transform.translate(-0.2f,0f,-0.5f));


    // The next are global variables so they can be updated in other methods
    translateX = new TransformNode("translate("+xPosition+",0,0)", Mat4Transform.translate(xPosition,0,0));
    translateZ = new TransformNode("translate(0,0,"+zPosition+")", Mat4Transform.translate(0,0,zPosition));
    rotateAll = new TransformNode("rotateAroundY("+rotateAllAngle+")", Mat4Transform.rotateAroundY(0));
    reverseRotateAll = new TransformNode("rotateAroundY("+rotateAllAngle+")", Mat4Transform.rotateAroundY(0));
    armWave_1 = new TransformNode("rotateAroundY("+rotateAllAngle+")", Mat4Transform.rotateAroundY(0));
    armWave_2 = new TransformNode("rotateAroundY("+rotateAllAngle+")", Mat4Transform.rotateAroundY(0));
    rotateUpper = new TransformNode("rotateAroundZ("+rotateUpperAngle+")",Mat4Transform.rotateAroundZ(rotateUpperAngle));
    rotateOnAxis = new TransformNode("rotateAroundY("+rotateSpeed+")",Mat4Transform.rotateAroundY(0));
    rotateOnAxis_2 = new TransformNode("rotateAroundY("+rotateSpeed+")",Mat4Transform.rotateAroundY(0));
    rotateOnAxis_3 = new TransformNode("rotateAroundY("+rotateSpeed+")",Mat4Transform.rotateAroundY(0));
    rotateOnAxis_4 = new TransformNode("rotateAroundY("+rotateSpeed+")",Mat4Transform.rotateAroundY(0));
    rotateOnAxis_5 = new TransformNode("rotateAroundY("+rotateSpeed+")",Mat4Transform.rotateAroundY(0));
    rotateOnAxis_6 = new TransformNode("rotateAroundY("+rotateSpeed_2+")",Mat4Transform.rotateAroundY(0));
    moveForward = new TransformNode("translate(0,0,"+moveForwardZ+")", Mat4Transform.translate(0,0,moveForwardZ));
    turnLeft =  new TransformNode("translate("+moveForwardX+",0,0)", Mat4Transform.translate(moveForwardX,0,0));


    /************************************** Scene Hierarchies ******************************************************/

    // Room walls Connected incase a need to rotate
    roomWallRoot.addChild(rightWall_1);
    roomWallRoot.addChild(ceiling_0);
    roomWallRoot.addChild(backWall);
    roomWallRoot.addChild(leftWall_1);
    roomWallRoot.addChild(leftWall_2);
    roomWallRoot.addChild(leftWall_3);
    roomWallRoot.addChild(leftWall_4);
    roomWallRoot.addChild(rotateOnAxis_6);
    rotateOnAxis_6.addChild(skyB);
    roomWallRoot.update();

  //Globe animation - continuous
    roomObjectsRoot.addChild(translateX);
    translateX.addChild(translateZ);
    translateZ.addChild(stand);
    stand.addChild(axiss);
    axiss.addChild(rotateOnAxis);
    rotateOnAxis.addChild(globe);
    roomObjectsRoot.update();


  // Robot 1 (Line moving robot)
    roombaRoot.addChild(turnLeft);
    turnLeft.addChild(translateToLine);
    translateToLine.addChild(moveForward);
    moveForward.addChild(rotateOnAxis_5);
    rotateOnAxis_5.addChild(rotateOnAxis_4);
    rotateOnAxis_4.addChild(rotateOnAxis_3);
    rotateOnAxis_3.addChild(rotateOnAxis_2);
    rotateOnAxis_2.addChild(track);
    track.addChild(translateRob2toLine);
    track.addChild(translateToTop);
    translateToTop.addChild(spotLight_p1);
    translateRob2toLine.addChild(miniRobot_1);
    miniRobot_1.addChild(translateEyeToL);
    translateEyeToL.addChild(rbt2_eye_1);
    miniRobot_1.addChild(translateEyeToR);
    translateEyeToR.addChild(rbt2_eye_2);
    roombaRoot.update();



  // Robot 2 (Dancing Robot)
    bigRobot.addChild(translateToStand);
    bigRobot.addChild(bigRobot_Stand);
    translateToStand.addChild(translateToFloor);
    translateToFloor.addChild(rotateAll);
    rotateAll.addChild(baseLeg);
    baseLeg.addChild(translateToBaseLegTop);
    translateToBaseLegTop.addChild(rotateUpper);
    rotateUpper.addChild(upperBaseLeg);
    upperBaseLeg.addChild(translateToUpperLeg);
    translateToUpperLeg.addChild(reverseRotateAll);
    reverseRotateAll.addChild(midBody);
    midBody.addChild(translateLeftArm);
    translateLeftArm.addChild(armWave_1);
    armWave_1.addChild(arm1);
    midBody.addChild(translateRightArm);
    translateRightArm.addChild(armWave_2);
    armWave_2.addChild(arm2);
    midBody.addChild(translateToMidBTop);
    translateToMidBTop.addChild(robotHead);
    robotHead.addChild(translateLeftHair);
    translateLeftHair.addChild(hair);
    robotHead.addChild(translateRightHair);
    translateRightHair.addChild(hair_2);
    robotHead.addChild(translateNose);
    translateNose.addChild(hole_1);
    robotHead.addChild(translateNose_2);
    translateNose_2.addChild(hole_2);
    bigRobot.update();


  }

  /************************************** End Scene Hierarchy ******************************************************/


  private SGNode makeRmWall(Model rWall) {
    NameNode rmWall = new NameNode("right room wall");
    Mat4 m = Mat4Transform.scale(20.0f,6.0f,0.2f);
    m = Mat4.multiply(Mat4Transform.rotateAroundY(90), m);
    m = Mat4.multiply(Mat4Transform.translate(4.9f,3.0f,0f), m);
    TransformNode rightWallB = new TransformNode("scale(20.0f,6.0f,0.2f); rotateAroundY(90); translate(4.9f,3.0f,0);", m);
    ModelNode wallNode = new ModelNode("Cube(0)", rWall);
    rmWall.addChild(rightWallB);
    rightWallB.addChild(wallNode);
    return rightWallB;
  }

  private SGNode makingCeiling(Model ceiling) {
    NameNode ceil = new NameNode("right room wall");
    Mat4 m = Mat4Transform.scale(10.0f,1.0f,20.0f);
    m = Mat4.multiply(Mat4Transform.translate(0.0f,6.0f,0f), m);
    TransformNode moveCeiling = new TransformNode("scale(20.0f,6.0f,0.2f); rotateAroundY(90); translate(4.9f,3.0f,0);", m);
    ModelNode wallNode = new ModelNode("Cube(0)", ceiling);
    ceil.addChild(moveCeiling);
    moveCeiling.addChild(wallNode);
    return moveCeiling;
  }

  private SGNode makeRmWall3(Model wall) {
    NameNode backWall = new NameNode("Back room wall");
    Mat4 m = Mat4Transform.scale(10.0f,6.0f,0.2f);
    m = Mat4.multiply(Mat4Transform.translate(0.0f,3.0f,-10.0f), m);
    TransformNode backWallB = new TransformNode("scale(20.0f,6.0f,0.2f); rotateAroundY(90); translate(-4.9f,3.0f,0);", m);
    ModelNode wallNode = new ModelNode("Wall(0)", wall);
    backWall.addChild(backWallB);
    backWallB.addChild(wallNode);
    return backWallB;
  }

  private SGNode makeRmWall2_0(Model cube) {
    NameNode leftWall = new NameNode("Left room wall");
    Mat4 m = Mat4Transform.scale(20.0f,2.0f,0.2f);
    m = Mat4.multiply(Mat4Transform.rotateAroundY(90), m);
    m = Mat4.multiply(Mat4Transform.translate(-4.9f,1.0f,0), m);
    TransformNode leftWallB = new TransformNode("scale(20.0f,6.0f,0.2f); rotateAroundY(90); translate(-4.9f,3.0f,0);", m);
    ModelNode wallNode = new ModelNode("Cube(1)", cube);
    leftWall.addChild(leftWallB);
    leftWallB.addChild(wallNode);
    return leftWallB;
  }

  private SGNode makeRmWall2_1(Model cube) {
    NameNode topLeftWall = new NameNode("TopLeft room wall");
    Mat4 m = Mat4Transform.scale(20.0f,1.0f,0.2f);
    m = Mat4.multiply(Mat4Transform.rotateAroundY(90), m);
    m = Mat4.multiply(Mat4Transform.translate(-4.9f,5.5f,0), m);
    TransformNode leftWallB1 = new TransformNode("scale(20.0f,6.0f,0.2f); rotateAroundY(90); translate(-4.9f,5.5f,0);", m);
    ModelNode wallNode = new ModelNode("Cube(3)", cube);
    topLeftWall.addChild(leftWallB1);
    leftWallB1.addChild(wallNode);
    return leftWallB1;
  }

  private SGNode makeRmWall2_2(Model cube) {
    NameNode sideWallL = new NameNode("SideLeft room wall");
    Mat4 m = Mat4Transform.scale(7.0f,3.0f,0.2f);
    m = Mat4.multiply(Mat4Transform.rotateAroundY(90), m);
    m = Mat4.multiply(Mat4Transform.translate(-4.9f,3.5f,6.5f), m);
    TransformNode leftWallB2 = new TransformNode("scale(20.0f,6.0f,0.2f); rotateAroundY(90); translate(-4.9f,3.0f,0);", m);
    ModelNode wallNode = new ModelNode("Cube(4)", cube);
    sideWallL.addChild(leftWallB2);
    leftWallB2.addChild(wallNode);
    return leftWallB2;
  }

  private SGNode makeRmWall2_3(Model cube) {
    NameNode sideWallR = new NameNode("SideRight room wall");
    Mat4 m = Mat4Transform.scale(7.0f,3.0f,0.2f);
    m = Mat4.multiply(Mat4Transform.rotateAroundY(90), m);
    m = Mat4.multiply(Mat4Transform.translate(-4.9f,3.5f,-6.5f), m);
    TransformNode leftWallB3 = new TransformNode("scale(20.0f,6.0f,0.2f); rotateAroundY(90); translate(-4.9f,3.0f,0);", m);
    ModelNode wallNode = new ModelNode("Cube(5)", cube);
    sideWallR.addChild(leftWallB3);
    leftWallB3.addChild(wallNode);
    return leftWallB3;
  }

  private SGNode makeGlobeStand(Model stand) {
    NameNode standCube = new NameNode("Stand Cube");
    Mat4 m = Mat4Transform.scale(1.5f,1.5f,1.5f);
    m = Mat4.multiply(m,Mat4Transform.translate(0.0f,0.5f,0.0f));
    TransformNode globeStand = new TransformNode("scale(20.0f,6.0f,0.2f);translate(0.0f,1.0f,0.0f);", m);
    ModelNode globeNode = new ModelNode("Cube(4)", stand);
    standCube.addChild(globeStand);
    globeStand.addChild(globeNode);
    return standCube;
  }

  private SGNode makeGlobeAxis(Model sphere) {
    NameNode axisName = new NameNode("Stand axis");
    Mat4 m = Mat4Transform.scale(0.1f,4.0f,0.15f);
    m = Mat4.multiply(m, Mat4Transform.translate(0.0f,0.75f,0.0f));
    TransformNode globeAxisBranch = new TransformNode("scale(20.0f,6.0f,0.2f);translate(0.0f,1.0f,0.0f);", m);
    ModelNode globeNode = new ModelNode("Sphere(1)", sphere);
    axisName.addChild(globeAxisBranch);
    globeAxisBranch.addChild(globeNode);
    return axisName;
  }

  private SGNode makeGlobe(Model sphere) {
    NameNode globe = new NameNode("lower branch");
    Mat4 m = Mat4Transform.scale(2.0f,2.0f,2.0f);
    m = Mat4.multiply(m, Mat4Transform.translate(0.0f,1.7f,0.0f));
    TransformNode upperBranch = new TransformNode("scale(2.5,2.5,2.5); translate(0,0.5,0)", m);
    ModelNode sphereNode = new ModelNode("Sphere(2)", sphere);
    globe.addChild(upperBranch);
    upperBranch.addChild(sphereNode);
    return globe;
  }

  private SGNode makeRoomba(Model smallRobot) {
    NameNode roomba = new NameNode("Roomba");
    Mat4 m = Mat4Transform.scale(1.2f,0.5f,1.2f);
    m = Mat4.multiply(m,Mat4Transform.translate(0.0f,0.5f,0.0f));
    TransformNode roobaBody = new TransformNode("scale(0.5f,0.5f,1.0f);translate(12f,1.0f,0.0f);", m);
    ModelNode roombaNode = new ModelNode("robot(1)", smallRobot);
    roomba.addChild(roobaBody);
    roobaBody.addChild(roombaNode);
    return roobaBody;
  }

  private SGNode makeRobotStand(Model sphere) {
    NameNode standSphere = new NameNode("Robot Stand");
    Mat4 m = Mat4Transform.scale(1.5f,0.15f,1.5f);
    m = Mat4.multiply(m,Mat4Transform.translate(-1.0f,0.5f,-3.5f));
    TransformNode robotStand = new TransformNode("scale(1.5f,0.15f,1.5f);translate(-1.2f,0.5f,-3.5f);", m);
    ModelNode robotNode = new ModelNode("sphere(3)", sphere);
    standSphere.addChild(robotStand);
    robotStand.addChild(robotNode);
    return standSphere;
  }

  private SGNode makePole(Model sphere) {
    NameNode lowerBranchName = new NameNode("base lower body");
    Mat4 m = Mat4Transform.scale(0.2f,0.8f,0.2f);
    m = Mat4.multiply(m,Mat4Transform.translate(0.0f,0.5f,0.0f));
    TransformNode lowerBranch = new TransformNode("scale(2.5,4,2.5); translate(0,0.5,0)", m);
    ModelNode sphereNode = new ModelNode("Sphere(4)", sphere);
    lowerBranchName.addChild(lowerBranch);
    lowerBranch.addChild(sphereNode);
    return lowerBranchName;
  }

  private SGNode makeMidBody(Model sphere) {
    NameNode midBodyName = new NameNode("part of lower body");
    Mat4 m = Mat4Transform.scale(0.4f,1.2f,0.3f);
    m = Mat4.multiply(m,Mat4Transform.translate(0.0f,0.5f,0.0f));
    TransformNode midBody_2 = new TransformNode("scale(1.4f,3.1f,1.4f);translate(0,0.5,0)", m);
    ModelNode sphereNode = new ModelNode("Sphere(6)", sphere);
    midBodyName.addChild(midBody_2);
    midBody_2.addChild(sphereNode);
    return midBodyName;
  }

  private SGNode makeHead(Model sphere) {
    NameNode headName = new NameNode("part of the head");
    Mat4 m = Mat4Transform.scale(1.2f, 0.6f, 0.8f);
    m = Mat4.multiply(m,Mat4Transform.translate(0.0f,0.5f,0.0f));
    TransformNode headTransform = new TransformNode("scale(0.6f,1.8f,0.6f);translate(-3.75f,1.85f,-17.3f)", m);
    ModelNode sphereNode = new ModelNode("Sphere(6)", sphere);
    headName.addChild(headTransform);
    headTransform.addChild(sphereNode);
    return headName;
  }

  private SGNode makeArm(Model sphere) {
    NameNode armName = new NameNode("part of arm");

    Mat4 m = Mat4Transform.scale(1.0f, 0.15f, 0.15f); // Narrow and long
    m = Mat4.multiply(m,Mat4Transform.translate(0.0f,0.5f,0.0f));

    TransformNode armTransform = new TransformNode("scale(0.15f,1.0f,0.15f);translate(-3.0f,1.85f,-17.0f)", m);
    ModelNode sphereNode = new ModelNode("Sphere(6)", sphere);
    armName.addChild(armTransform);
    armTransform.addChild(sphereNode);
    return armName;
  }

  private SGNode makeArm2(Model sphere) {
    NameNode arm2Name = new NameNode("part of arm");
    Mat4 m = Mat4Transform.scale(1.0f, 0.15f, 0.15f);
    m = Mat4.multiply(m,Mat4Transform.translate(0.0f,0.5f,0.0f));
    TransformNode armTransform = new TransformNode("scale(0.15f,1.0f,0.15f);translate(-3.0f,1.85f,-17.0f)", m);
    ModelNode sphereNode = new ModelNode("Sphere(7)", sphere);
    arm2Name.addChild(armTransform);
    armTransform.addChild(sphereNode);
    return arm2Name;
  }

  private SGNode makeHair(Model sphere) {
    NameNode hairName = new NameNode("part of Head");
    Mat4 m = Mat4Transform.scale(0.15f, 0.7f, 0.15f);
    m = Mat4.multiply(m,Mat4Transform.translate(0.0f,0.5f,0.0f));
    TransformNode hairTransform2 = new TransformNode("scale(0.15f,1.0f,0.15f);translate(-3.0f,1.85f,-17.0f)", m);
    ModelNode sphereNode = new ModelNode("Sphere(8)", sphere);

    hairName.addChild(hairTransform2);
    hairTransform2.addChild(sphereNode);
    return hairName;
  }

  private SGNode makeNose(Model sphere) {
    NameNode noseName = new NameNode("part of Head");
    Mat4 m = Mat4Transform.scale(0.12f, 0.12f, 0.12f);
    m = Mat4.multiply(m,Mat4Transform.translate(0.0f,0.5f,0.0f));
    TransformNode noseTransform = new TransformNode("scale(0.15f,1.0f,0.15f);translate(-3.0f,1.85f,-17.0f)", m);
    ModelNode sphereNode = new ModelNode("Sphere(8)", sphere);
    noseName.addChild(noseTransform);
    noseTransform.addChild(sphereNode);
    return noseName;
  }

  private SGNode makeSpotLight_1(Model sphere) {
    NameNode noseName = new NameNode("part of Head");
    Mat4 m = Mat4Transform.scale(0.4f, 0.07f, 0.25f);
    m = Mat4.multiply(m,Mat4Transform.translate(0.0f,0.5f,0.0f));
    TransformNode noseTransform = new TransformNode("scale(0.15f,1.0f,0.15f);translate(-3.0f,1.85f,-17.0f)", m);
    ModelNode sphereNode = new ModelNode("Sphere(8)", sphere);
    noseName.addChild(noseTransform);
    noseTransform.addChild(sphereNode);
    return noseName;
  }

  private SGNode makeSky(Model skyBox) {
    NameNode noseName = new NameNode("part of Head");
    Mat4 m = Mat4Transform.scale(70f, 70f, 70.0f);
    TransformNode noseTransform = new TransformNode("scale(0.15f,1.0f,0.15f);translate(-3.0f,1.85f,-17.0f)", m);
    ModelNode sphereNode = new ModelNode("Sphere(8)", skyBox);
    noseName.addChild(noseTransform);
    noseTransform.addChild(sphereNode);
    return noseName;
  }

  private SGNode makeEyes_1(Model sphere) {
    NameNode noseName = new NameNode("part of Roomba");
    Mat4 m = Mat4Transform.scale(0.15f, 0.15f, 0.1f);
    m = Mat4.multiply(m,Mat4Transform.translate(0.0f,0.5f,0.0f));
    TransformNode noseTransform = new TransformNode("scale(0.15f,1.0f,0.15f);translate(-3.0f,1.85f,-17.0f)", m);
    ModelNode sphereNode = new ModelNode("Sphere(9)", sphere);
    noseName.addChild(noseTransform);
    noseTransform.addChild(sphereNode);
    return noseName;
  }

  private SGNode makeTrack(Model sphere) {
    NameNode noseName = new NameNode("part of Roomba");
    Mat4 m = Mat4Transform.scale(0.09f, 1.4f, 0.09f);
    m = Mat4.multiply(m,Mat4Transform.translate(0.0f,0.5f,0.0f));
    TransformNode noseTransform = new TransformNode("scale(0.15f,1.0f,0.15f);translate(-3.0f,1.85f,-17.0f)", m);
    ModelNode sphereNode = new ModelNode("Sphere(9)", sphere);
    noseName.addChild(noseTransform);
    noseTransform.addChild(sphereNode);
    return noseName;
  }





  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    double elapsedTime = getSeconds() - startTime;


    light.setPosition(getLightPosition());  // changing light position each frame
    light.render(gl);
    floor.render(gl);


    // 5. animation. Comment out the following line for 1-4.
    updateBranches();

    roomWallRoot.draw(gl);
    roomObjectsRoot.draw(gl);
    roombaRoot.draw(gl);
    bigRobot.draw(gl);

  }


  private void updateBranches() {
    float totalCycleTime = 23.7f;
    double elapsedTime = (getSeconds() - startTime) % totalCycleTime;
    float turningSpeed = 45.0f;

// Roomba Movement
    float speedZ = 3.0f;  // Adjust as needed for speed
    float speedX = 3.0f;  // Adjust as needed for speed
      // Total duration of one cycle

// Calculate cycle time causing continuous motion and turing
    float cycleTime = (float)elapsedTime % totalCycleTime;


    if (cycleTime < 2.7f) {

      moveForwardZ = cycleTime * speedZ; // Update Z
      moveForward.setTransform(Mat4Transform.translate(0.0f, 0.0f, -moveForwardZ));

    }else if (cycleTime >=2.7 && cycleTime <4.7) {
      float timeInXDirection = cycleTime - 2.7f;
      rotateOnAxis_2.setTransform(Mat4Transform.rotateAroundY(timeInXDirection*turningSpeed));
    }
    else if (cycleTime >= 4.7f && cycleTime < 7.0f) {
      float timeInXDirection = cycleTime - 4.7f;
      moveForwardX = timeInXDirection * speedX;
      turnLeft.setTransform(Mat4Transform.translate(-moveForwardX, 0.0f, 0));

    }else if (cycleTime >= 7.0f && cycleTime < 9.0f){
      float timeInXDirection = cycleTime - 7.0f;
      rotateOnAxis_3.setTransform(Mat4Transform.rotateAroundY(timeInXDirection*turningSpeed));
    }
    else if (cycleTime >= 9.0f && cycleTime < 14.5f) {
      float timeInZDirection = cycleTime - 9.0f;
      moveForwardZ = -((2.7f * speedZ) - (timeInZDirection * speedZ));
      moveForward.setTransform(Mat4Transform.translate(0, 0.0f, moveForwardZ));

    }else if(cycleTime >= 14.5f && cycleTime < 16.5f) {
      float timeInXDirection = cycleTime - 14.5f;
      rotateOnAxis_4.setTransform(Mat4Transform.rotateAroundY(timeInXDirection*turningSpeed));
    }
    else if (cycleTime >= 16.5f && cycleTime < 18.8f) {
      float timeInXDirection = cycleTime - 16.5f;
      moveForwardX = -6.9f + (timeInXDirection * speedX);
      turnLeft.setTransform(Mat4Transform.translate(moveForwardX, 0.0f, 0.0f));

    }else if(cycleTime >= 18.8f && cycleTime < 20.8f) {
      float timeInXDirection = cycleTime - 18.8f;
      rotateOnAxis_5.setTransform(Mat4Transform.rotateAroundY(timeInXDirection*turningSpeed));
    }
    else if (cycleTime >= 20.8f && cycleTime < 23.7f) {
      float timeInXDirection = cycleTime - 20.8f;
      moveForwardZ = -8.4f + (timeInXDirection * speedX);
      moveForward.setTransform(Mat4Transform.translate(0, 0.0f, -moveForwardZ));
    }


    // rotation of globe
    globeRotationAngle = rotateSpeed * (float)elapsedTime;
    rotateOnAxis.setTransform(Mat4Transform.rotateAroundY(globeRotationAngle));

    skyrotate = rotateSpeed_2 * (float)elapsedTime;
    rotateOnAxis_6.setTransform(Mat4Transform.rotateAroundY(skyrotate));



    float tiltConst = 1.5f;
    float danceTiltConst = 2.0f;

    rotateAllAngle = rotateAllAngleStart * (float) Math.sin(elapsedTime);
    // Opposite oscillating angle for upper branch
    rotateUpperAngle = -rotateUpperAngleStart * (float) Math.sin(elapsedTime);

    //Normal Dance cycle for robot
    rotateAll.setTransform(Mat4Transform.rotateAroundZ(-rotateAllAngle));
    rotateUpper.setTransform(Mat4Transform.rotateAroundZ(rotateUpperAngle));
    reverseRotateAll.setTransform(Mat4Transform.rotateAroundZ(-rotateAllAngle ));
    armWave_1.setTransform(Mat4Transform.rotateAroundZ(-rotateAllAngle * tiltConst));

    //When roomba is near Dancing Robot
    if(elapsedTime >=5.5f && elapsedTime <12.0f){
      rotateAll.setTransform(Mat4Transform.rotateAroundY(-rotateAllAngle* danceTiltConst));
      rotateUpper.setTransform(Mat4Transform.rotateAroundZ(rotateUpperAngle));
      reverseRotateAll.setTransform(Mat4Transform.rotateAroundX(-rotateAllAngle * danceTiltConst));
      armWave_1.setTransform(Mat4Transform.rotateAroundZ(-rotateAllAngle * danceTiltConst));
      armWave_2.setTransform(Mat4Transform.rotateAroundX(rotateAllAngle * danceTiltConst));
    }

    roomWallRoot.update();
    roomObjectsRoot.update();
    roombaRoot.update();
    bigRobot.update();
  }


  // The light's position is continually being changed, so needs to be calculated for each frame.
  // (From Steve Maddox Lab Code)
  private Vec3 getLightPosition() {
    double elapsedTime = getSeconds()-startTime;
    float x = 5.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
    float y = 2.7f;
    float z = 5.0f*(float)(Math.cos(Math.toRadians(elapsedTime*50)));
    return new Vec3(x,y,z);
    //return new Vec3(5f,3.4f,5f);
  }


//Models to make Base shapes
  private Model makeFloor(GL3 gl, Texture t1, Texture t2) {
    String name = "floor";
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_1t.txt");
    Material material = new Material(new Vec3(0.8f, 0.5f, 0.81f), new Vec3(0.9f, 0.5f, 0.81f), new Vec3(0.8f, 0.8f, 0.8f), 40.0f);
    Mat4 modelMatrix = Mat4Transform.scale(10,1f,20);
    Model floor = new Model(name, mesh, modelMatrix, shader, material, light, camera, t1, t2);
    return floor;
  }

  private Model makePlane(GL3 gl, Texture t1) {
    String name = "ceiling";
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_1t.txt");
    Material material = new Material(new Vec3(0.8f, 0.5f, 0.81f), new Vec3(0.9f, 0.5f, 0.81f), new Vec3(0.8f, 0.8f, 0.8f), 40.0f);
    Mat4 modelMatrix = Mat4Transform.scale(10,1f,20);
    Model ceiling = new Model(name, mesh, modelMatrix, shader, material, light, camera, t1);
    return ceiling;
  }

  private Model makeCube(GL3 gl, Texture t1, Texture t2) {
    String name= "cube";
    Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_2t.txt");
    Material material = new Material(new Vec3(1.0f, 0.5f, 0.2f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    Model cube = new Model(name, mesh, new Mat4(1), shader, material, light, camera, t1, t2);
    return cube;
  }

  private Model makeBackWall(GL3 gl, Texture t1, Texture t2) {
    String name= "wall";
    Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_2t.txt");
    Material material = new Material(new Vec3(1.0f, 0.5f, 0.2f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    Model wall = new Model(name, mesh, new Mat4(1), shader, material, light, camera, t1, t2);
    return wall;
  }

  private Model makeRightWall(GL3 gl, Texture t1) {
    String name = "rWall";
    Mesh mesh = new Mesh(gl, Wall.vertices.clone(), Wall.indices.clone());
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_2t.txt");
    Material material = new Material(new Vec3(1.0f, 0.5f, 0.2f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    Model rWall = new Model(name, mesh, new Mat4(1), shader, material, light, camera, t1);
    return rWall;
  }

  private Model makeSmallRobot(GL3 gl, Texture t1) {
    String name= "smallRobot";
    Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_2t.txt");
    Material material = new Material(new Vec3(1.0f, 0.5f, 0.2f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    Model smallRobot = new Model(name, mesh, new Mat4(1), shader, material, light, camera, t1);
    return smallRobot;
  }

  private Model makeStand(GL3 gl, Texture t1, Texture t2) {
    String name= "stand";
    Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_2t.txt");
    Material material = new Material(new Vec3(1.0f, 0.5f, 0.2f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    Model stand = new Model(name, mesh, new Mat4(1), shader, material, light, camera, t1, t2);
    return stand;
  }

  private Model makeSphere(GL3 gl, Texture t1) {
    String name = "sphere";
    Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_2t.txt");
    Material material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    Model sphere = new Model(name, mesh, new Mat4(1), shader, material, light, camera, t1 );
    return sphere;
  }

  private Model makeSky(GL3 gl, Texture t1) {
    String name= "sky";
    Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_2t.txt");
    Material material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    Model sky = new Model(name, mesh, new Mat4(1), shader, material, light, camera, t1 );
    return sky;
  }

  private Model makeSphere2(GL3 gl, Texture t1, Texture t2) {
    String name = "sphere";
    Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_2t.txt");
    Material material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    Model sphere = new Model(name, mesh, new Mat4(1), shader, material, light, camera, t1, t2);
    return sphere;
  }




  // ***************************************************
  /* TIME
   */

  private double startTime;

  private double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }

  // ***************************************************
  /* An array of random numbers
   */

  private int NUM_RANDOMS = 1000;
  private float[] randoms;

  private void createRandomNumbers() {
    randoms = new float[NUM_RANDOMS];
    for (int i=0; i<NUM_RANDOMS; ++i) {
      randoms[i] = (float)Math.random();
    }

  }

}